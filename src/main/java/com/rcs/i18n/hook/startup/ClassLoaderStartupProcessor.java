package com.rcs.i18n.hook.startup;


import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

public class ClassLoaderStartupProcessor extends SimpleAction {

    private static final Logger _logger = Logger.getLogger(ClassLoaderStartupProcessor.class);

    private static final String CLASSES_TO_LOAD[] = { "com.liferay.portal.kernel.util.MessageSourceUtil",
                                                      "com.liferay.portal.kernel.util.ObjectFactory",
                                                      "com.liferay.portal.kernel.util.ResourceBundleUtil",
                                                      "com.liferay.portal.util.PortletCategoryKeys",
                                                      "com.liferay.portal.util.PortalUtilExt"
    };

    public static final String CLASS_URLS_TO_LOAD[] =  {"com/liferay/portal/kernel/util/MessageSourceUtil.class",
                                                        "com/liferay/portal/kernel/util/ObjectFactory.class",
                                                        "com/liferay/portal/kernel/util/ResourceBundleUtil.class",
                                                        "com/liferay/portal/util/PortletCategoryKeys.class",
                                                        "com/liferay/portal/util/PortalUtilExt.class"
    };


    public static final String STRUTS_RESOURCE_BUNDLE_CLASS = "com.liferay.portlet.StrutsResourceBundle";
    public static final String STRUTS_RESOURCE_BUNDLE_URL   = "com/liferay/portlet/StrutsResourceBundle.class";
        public static final String STRUTS_RESOURCE_BUNDLE_BODY  =
            "System.out.println('STRUTS_RESOURCE_BUNDLE Start');\n" +
            "        if (key == null) {\n" +
            "            throw new NullPointerException();\n" +
            "        }\n" +
            "        if ((key.equals(JavaConstants.JAVAX_PORTLET_DESCRIPTION) ||\n" +
            "                key.equals(JavaConstants.JAVAX_PORTLET_KEYWORDS) ||\n" +
            "                key.equals(JavaConstants.JAVAX_PORTLET_LONG_TITLE) ||\n" +
            "                key.equals(JavaConstants.JAVAX_PORTLET_SHORT_TITLE) ||\n" +
            "                key.equals(JavaConstants.JAVAX_PORTLET_TITLE))) {\n" +
            "            key = key.concat(StringPool.PERIOD).concat(_portletName);\n" +
            "        }\n" +
            "        String value = StringUtils.EMPTY;\n" +
            "        try {\n" +
            "            value = ResourceBundleUtil.getString(_locale, key);\n" +
            "        } catch (Exception e) {\n" +
            "            _logger.error(e.getMessage());\n" +
            "        }\n" +
            "        if (StringUtils.isBlank(value))\n" +
            "            LanguageUtil.get(_locale, key);\n" +
            "        if ((value == null) && ResourceBundleThreadLocal.isReplace()) {\n" +
            "            value = ResourceBundleUtil.NULL_VALUE;\n" +
            "        }\n" +
            "System.out.println('STRUTS_RESOURCE_BUNDLE End');\n" +
            "        return value;";

    @Override
    public void run(String[] ids) throws ActionException {

        //current class loader
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        //liferay class loader
        ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

        //replace current classLoader
        Thread.currentThread().setContextClassLoader(portalClassLoader);

        int counter = 0;
        for (String classUrlToLoad : CLASS_URLS_TO_LOAD) {

            String className = CLASSES_TO_LOAD[counter];

            URL classURL = contextClassLoader.getResource(classUrlToLoad);

            File classFile = new File(classURL.getFile());

            InputStream is = null;
            try {
                is = classURL.openStream();
            } catch (IOException e) {
                _logger.warn("Can not opern stream.");
                counter++;
                continue;
            }

            byte[] data = null;
            try {
                data = IOUtils.toByteArray(is);
            } catch (IOException e) {
                _logger.warn("Can not get byte array.");
                counter++;
                continue;
            }

            int off = 0;

            int len = (int)classFile.length();

            try {
                Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                method.setAccessible(true);
                method.invoke(portalClassLoader, className, data, off, len);
            } catch (Exception e) {
                _logger.warn("Can not add class to portal classLoader.");
            }

            counter++;
        }

        /*======== JAVA_ASSIST =======*/

            try {

                /*== Method change ==*/
                ClassPool pool = ClassPool.getDefault();
                pool.insertClassPath(new ClassClassPath(this.getClass()));
                CtClass cc = pool.get(STRUTS_RESOURCE_BUNDLE_CLASS);
                CtMethod cm = cc.getDeclaredMethod("handleGetObject");

                cm.insertBefore("System.out.println(1111);");


                /*
                cm.instrument(new ExprEditor(){
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        if (m.getClassName().equals(STRUTS_RESOURCE_BUNDLE_CLASS) &&
                            m.getMethodName().equals("handleGetObject")) {
                            m.replace(STRUTS_RESOURCE_BUNDLE_BODY);
                        }
                    }


                });

                cm.insertAfter("System.out.println(2222);");
                */

                cc.writeFile();

                cc.toClass();


            } catch (Exception e) {
                _logger.warn("Exception in JavaAssist: " + e.getMessage());
            }


        /*======== JAVA_ASSIST end =======*/

        PortalClassLoaderUtil.setClassLoader(portalClassLoader);

        //return original classLoader
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
}

<%@ include file="init-view.jsp" %>

<script type="text/javascript">

    jQuery(document).ready(function($){

        jQuery.ajax({
            url: '${resourceContentURL}' + '&startIndex=0&pageSize=10',
            type: 'POST',
            dataType: 'json',
            success: init
        });

        function init(data){
            var editor = new ResourceEditor({
                namespace : '<portlet:namespace/>',
                uploadResourcesURL: '${uploadResourcesURL}',
                resourceContentURL: '${resourceContentURL}',
                deleteURL: '${deleteURL}',
                editURL: '${editURL}',
                deleteMSG: '<liferay-ui:message key="delete"/>',
                mswJson : data,
                itemCount : 10
            });
        }

    });

</script>
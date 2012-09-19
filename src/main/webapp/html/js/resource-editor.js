;
(function ($) {

    $.ajaxSetup({
        mimeType:"text/html;charset=UTF-8"
    })

    var Utils = {

        _getRealId:function (namespase, id) {
            var realId = namespase + id;
            return realId;
        },

        _getObjById:function (id) {
            return $('#' + id);
        },

        _sendAjax:function (url, data, success) {
            $.ajax({
                url:url + '&rand=' + Math.random(),
                type:'POST',
                mimeType:'text/html;charset=UTF-8',
                data:'data=' + data,
                dataType: 'json',
                success:success
            });
        },

        _getJson:function (url, data, success) {
            $.ajax({
                url:url + data + '&rand=' + Math.random(),
                type:'POST',
                mimeType:'text/html;charset=UTF-8',
                dataType:'json',
                success:success
            });
        },

        _toggleElementWithDelay:function(element, view, delay){
            $(element)
                .show(view)
                .delay(delay)
                .hide(view);

        }
    };

    var ResourceEditor = function (config) {
        this.mswContentId = "mswContent";
        this.saveButtonClass = "save-button";
        this.newButtonClass = 'new-button';
        this.paginatorId = "paginator";
        this.paginContainerId = "paginContainer";
        this.loadImgId = "loadImg";
        this.messageContainerId = "messageContainer";
        this.defauldSize = 10;

        this.searchButtonId  = "searchButton";
        this.searchFormId    = "filterTranslationsForm";
        this.onKeyInput      = "onKeyInput";
        this.onMessageInput  = "onMessageInput";
        this.onMessageSelect = "onMessageSelect";
        this.bundleSelect    = "bundlesSelect";

        this.extraData = "";

        $this = this;
        $globalScope = this;
        this._init(config);
    };

    var EditResourceKey= function (config){
        this.keyInputId = "key";
        this.contentId = "content";
        this.saveButtonClass = "save-button";
        this.formId = "fm";
        $this = this;
        this._init(config);
    };

    $.extend(true, ResourceEditor, {
        prototype:{
            _init:function (config) {

                var editor = this;

                editor.configuration = config;

                editor._initUI();

                editor._bindEvents();
            },

            _initUI:function () {

                var editor = this;

                editor._loadContent();

            },

            _bindEvents:function () {

                var editor = this;
                //MessageSourceWrappers container
                var contentBox = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.mswContentId));

                contentBox.on('change', 'select', editor._selectOnChange);

                contentBox.on('focusout', 'input.content', editor._inputOnFocusOut);

                contentBox.on('click', 'a.delete', editor._delete);

                $('.' + editor.saveButtonClass).on('click', $.proxy(editor._saveResources, editor));

                $('.' + editor.newButtonClass).on('click', editor._editResource);

                var paginatorContainer = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.paginContainerId));

                paginatorContainer.on("click", "a", editor._afterPaginClicked);

                var searchButton = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.searchButtonId));

                searchButton.on("click", editor._searchTranslations);

                var searchForm = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.searchFormId));

                searchForm.delegate("input[type=text]", "keypress", editor._searchKeyPressHandler);
            },

            _searchKeyPressHandler: function(event) {

                // if enter was pressed
                if (event.keyCode == 13) {

                    var editor = $globalScope;

                    var searchButton = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.searchButtonId));

                    searchButton.trigger("click");
                }
            },

            _searchTranslations: function() {

                var scope = $globalScope;

                var onKeyText = Utils._getObjById(Utils._getRealId(scope.configuration.namespace, scope.onKeyInput)).val();
                var onMessageText = Utils._getObjById(Utils._getRealId(scope.configuration.namespace, scope.onMessageInput)).val();
                var messageLocale = Utils._getObjById(Utils._getRealId(scope.configuration.namespace, scope.onMessageSelect)).val();
                var messageBundle = Utils._getObjById(Utils._getRealId(scope.configuration.namespace, scope.bundleSelect)).val();

                scope.extraData = "&resourcekey=" + onKeyText + "&resourcemessage=" + onMessageText
                    + "&resourcelocale=" + messageLocale + "&resourcebundle=" + messageBundle;

                $('.jPag-current').removeClass('jPag-current');

                $(".jPag-pages").children().eq(0).addClass("jPag-current");

                var currentPage = $('.jPag-current').text();

                scope._searchContent(currentPage, scope.extraData);
            },

            //get selected page number and reload content using it
            _afterPaginClicked:function(e){

                var element = $(e.target);

                $globalScope._toggleImg(true);

                var text = element.text();
                var idx = 1;

                if(text === 'First'){

                }else if(text === 'Last'){

                    var last = Utils._getObjById(Utils._getRealId($globalScope.configuration.namespace, $globalScope.paginatorId)).find('li:last-child');
                    idx = last.text();

                } else {
                    idx = text;

                }
                $globalScope._reloadContent(idx);
            },

            _toggleImg: function(show){
                var imgContainer = Utils._getObjById(Utils._getRealId($globalScope.configuration.namespace, $globalScope.loadImgId));
                if(show){
                    imgContainer.removeClass('none');
                }else{
                    imgContainer.addClass('none');
                }
            },

            _toggleSaveButton: function(disable){
                var saveButton = $('.' + $globalScope.saveButtonClass);
                if(disable){
                    saveButton.attr('disabled', true);
                }else{
                    saveButton.removeAttr('disabled');
                }
            },

            //init pagination
            _viewPagination:function(count, start, pageSize){
                var pageCount = Math.ceil(count/pageSize);
                var startPage = start/pageSize + 1;
                Utils._getObjById(Utils._getRealId($globalScope.configuration.namespace, $globalScope.paginatorId)).paginate({
                    count 		: pageCount,
                    start 		: startPage,
                    display     : pageSize,
                    border					: true,
                    border_color			: '#fff',
                    text_color  			: '#fff',
                    background_color    	: 'grey',
                    border_hover_color		: '#ccc',
                    text_hover_color  		: '#000',
                    background_hover_color	: '#fff',
                    images					: false,
                    mouse					: 'press'
                });
            },

            //go to resource edit page
            _editResource: function(){
                window.location.href = $globalScope.configuration.editURL;
            },

            //delete row
            _delete: function(){
                if(confirm($globalScope.configuration.deleteMSG)){
                    var $a = $(this);
                    var key = $a.closest('tr').children('td').eq(1).children('input[type=text]').val();
                    Utils._sendAjax($globalScope.configuration.deleteURL, key, $globalScope._reloadAll);
                }
                return false;
            },

            _reloadAll: function(){
                window.location.reload();
            },

            _searchContent: function(page,extraData) {
                var editor = this;
                var data = '&startIndex=' + (page*this.defauldSize - this.defauldSize)  + '&pageSize=' + this.defauldSize;
                if (extraData !== undefined)
                    data += extraData;
                var json = Utils._getJson(editor.configuration.resourceContentURL, data, editor._vewSearchContent);
            },

            _vewSearchContent: function(json) {
                var scope = $globalScope;
                scope._viewContent(json);
                scope._viewPagination(json.totalRecords, json.start, scope.defauldSize);
            },

            _reloadContent: function(page){
                var editor = this;
                if (page === "0")
                    page = 1;
                var startIndex = page*this.defauldSize - this.defauldSize;
                var pageSize = this.defauldSize;
                var data = '&startIndex=' + startIndex  + '&pageSize=' + pageSize;
                if ($globalScope.extraData !== undefined)
                    data += $globalScope.extraData;
                Utils._getJson(editor.configuration.resourceContentURL, data, editor._viewContent);
            },

            //view content and pagination
            _loadContent: function(){
                var editor = $this;
                var json = editor.configuration.mswJson
                editor._viewContent(json);
                editor._viewPagination(json.totalRecords, json.start, this.defauldSize);
            },

            _viewContent: function(json){

                var editor = $globalScope;
                var contentBox = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.mswContentId));
                var contentTable = $('<table/>');

                $.each(json.records, function(idx, obj){

                    var inputId = editor.configuration.namespace + '_resource_' + idx;
                    var firstKey = '';

                    $.each(obj.source, function (sIdx, sourceItm) {
                        firstKey = sourceItm;
                        return false;
                    });

                    var tr = $('<tr/>');
                    var keyInput = $('<input/>',{
                        type:'text',
                        value:obj.key,
                        readonly:''
                    });


                    var bundleInput = $('<input/>',{
                        type: 'text',
                        value: obj.bundle,
                        readonly:''
                    });

                    var td0 = $('<td/>');
                    td0.append(bundleInput);
                    tr.append(td0);

                    var td1 = $('<td/>');
                    td1.append(keyInput);
                    tr.append(td1);

                    var resourceInput = $('<input/>',{
                        'class':'content',
                        id: inputId,
                        type:'text',
                        name:obj.key,
                        value:firstKey
                    });

                    var td2 = $('<td/>');
                    td2.append(resourceInput);
                    tr.append(td2);

                    var select = $('<select/>', {
                        name: obj.key
                    });

                    $.each(obj.source, function (sIdx, sourceItm) {
                        var option =  $('<option/>',{
                            'data-key': obj.key,
                            'data-value': ' ',
                            value:sourceItm,
                            text: sIdx
                        });
                        select.append(option);
                    });

                    var td3 = $('<td/>');
                    td3.append(select);
                    tr.append(td3);

                    var delButton = $('<a/>', {
                        'class': 'delete',
                        'href': '#'
                    });

                    var img = $('<img/>',{
                        title: 'Delete',
                        alt: 'Delete',
                        src: '/html/themes/classic/images/common/delete.png',
                        'class': 'icon'
                    });

                    delButton.append(img);

                    var td4 = $('<td/>');
                    td4.append(delButton);
                    tr.append(td4);

                    contentTable.append(tr);
                });

                contentBox.empty().append(contentTable);
                editor._toggleImg(false);
                editor._toggleSaveButton(false);
            },

            //When select changed set MessageSource value to input from selected option
            _selectOnChange: function(){
                var $select = $(this);
                $select
                    .closest('tr')
                    .children('td')
                    .eq(2)
                    .children('input[type=text]')
                    .val($select.val());
            },

            //When input stopped editing and focus out, get input value and set in to relevant option
            //and set options 'data-value' to 'changed'
            _inputOnFocusOut: function(){
                var $input = $(this);
                var inputVal = $input.val();
                var option = $input
                    .closest('tr')
                    .children('td')
                    .eq(3)
                    .children('select')
                    .children('option:selected');
                option
                    .val(inputVal)
                    .attr('data-value', 'changed');
            },

            //get all options that was changed, serialize them and post via ajax
            _saveResources:function () {
                var editor = this;
                var changedOptionArr = $('option[data-value=changed]');
                var arr = [];
                $.each(changedOptionArr, function(idx, option){
                    var $option = $(option);
                    arr[arr.length] = {
                        'key':$option.attr('data-key'),
                        'value': $option.val(),
                        'locale': $option.text()
                    };
                });
                var json = JSON.stringify(arr);
                Utils._sendAjax(editor.configuration.uploadResourcesURL, json, editor._afterResourcesUploaded);
                editor._toggleSaveButton(true);
            },

            //get current selected page and reload content using it
            _afterResourcesUploaded: function(response){
                var currentPage = $('.jPag-current').text();
                $globalScope._writeMessages(response);
                $globalScope._reloadContent(currentPage);
            },

            _writeMessages: function(data){
                var messageContainer = Utils._getObjById(Utils._getRealId($globalScope.configuration.namespace, $globalScope.messageContainerId));
                messageContainer.empty();
                $.each(data, function(idx, msg){
                    var msgClass = '';
                    if(msg.error){
                        msgClass = 'portlet-msg-error';
                    }else{
                        msgClass = 'portlet-msg-success';
                    }
                    var msgSpan = $('<span/>', {
                        'class': msgClass,
                        text: msg.message
                    });
                    messageContainer.append(msgSpan);
                });
                Utils._toggleElementWithDelay(messageContainer, 'slow', 5000);
            }
        }
    });

    $.extend(true, EditResourceKey, {
        prototype:{
            _init: function(config){
                var editor = this;

                editor.configuration = config;

                editor._initUI();
                editor._bindEvents();

            },
            _initUI:function(){

            },
            _bindEvents: function(){
                var editor = this;
                $('.' + editor.saveButtonClass).on('click', $.proxy(editor._saveResources, editor));
            },

            _saveResources: function(){
                var editor = this;
                var key = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.keyInputId));

                var isKeyValid = editor._validateKey(key);
                if(isKeyValid){
                    var json = editor._serializeTable();
                    Utils._sendAjax(editor.configuration.saveURL, json, $.proxy(editor._redirectBack, editor));
                }

            },

            _redirectBack: function(){
                var editor = $this;
                window.location.href = editor.configuration.defaultURL;
            },

            _serializeTable: function(){
                var trArr = Utils._getObjById(Utils._getRealId($this.configuration.namespace, $this.contentId)).find('tr');
                var keyVal = Utils._getObjById(Utils._getRealId($this.configuration.namespace, $this.keyInputId)).val();
                var arr = [];
                $.each(trArr, function(idx, tr){
                    var $tr = $(tr);
                    arr[arr.length] = {
                        'key': keyVal,
                        'value': $tr.children('td').eq(0).children('input[type=text]').val(),
                        'locale': $tr.children('td').eq(1).children('input[type=text]').val()
                    }
                });
                return JSON.stringify(arr);
            },

            _validateKey: function(key){
                var keyVal = $(key).val();
                if(keyVal == ''){
                    $('.error').removeClass('none');
                    return false;
                }else{
                    $('.error').addClass('none');
                    return true;
                }
            }
        }
    });

    window.ResourceEditor = ResourceEditor;
    window.EditResourceKey = EditResourceKey;

})(jQuery);

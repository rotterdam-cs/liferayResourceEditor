;
(function ($) {

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
                url:url,
                type:'POST',
                mimeType:'text/html;charset=UTF-8',
                data:'data=' + data + '&rand=' + Math.random(),
                success:success
            });
        },

        _getJson:function (url, data, success) {
            $.ajax({
                url:url + data,
                type:'POST',
                mimeType:'text/html;charset=UTF-8',
                dataType:'json',
                success:success
            });
        },

        _createProxyListener:function (listener, scope, args) {

            return function () {

                return listener.call(scope, args);

            }

        }
    };

    var ResourceEditor = function (config) {
        this.mswContentId = "mswContent";
        this.saveButtonClass = "save-button";
        this.newButtonClass = 'new-button';
        this.paginatorId = "paginator";
        this.paginContainerId = "paginContainer";
        this.defauldSize = 10;
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

                $('.' + editor.saveButtonClass).on('click', Utils._createProxyListener(editor._saveResources, editor));

                $('.' + editor.newButtonClass).on('click', editor._editResource);

                var paginatorContainer = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.paginContainerId));
                paginatorContainer.on('click', 'a', editor._afterPaginClicked);

            },

            //get selected page number and reload content using it
            _afterPaginClicked:function(){
                var $a = $(this);
                var text = $a.text();
                var idx = 1;
                if(text == 'First'){

                }else if(text == 'Last'){
                    var last = Utils._getObjById(Utils._getRealId($globalScope.configuration.namespace, $globalScope.paginatorId)).find('li:last-child');
                    idx = last.text();
                } else {
                    idx = text;
                }
                $globalScope._reloadContent(idx);
            },

            //init pagination
            _viewPagination:function(count, start, pageSize){
                var pageCount = count/pageSize;
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
                    var key = $a.closest('tr').children('td').eq(0).children('input[type=text]').val();
                    Utils._sendAjax($globalScope.configuration.deleteURL, key, $globalScope._reloadAll);
                }
                return false;
            },

            _reloadAll: function(){
                window.location.reload();
            },

            _reloadContent: function(page){
                var editor = this;
                var data = '&startIndex=' + (page*this.defauldSize - this.defauldSize)  + '&pageSize=' + this.defauldSize;
                var json = Utils._getJson(editor.configuration.resourceContentURL, data, editor._viewContent);
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
                        return;
                    });

                    var tr = $('<tr/>');
                    var keyInput = $('<input/>',{
                        type:'text',
                        value:obj.key,
                        readonly:''
                    });

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
                            'data-value': '',
                            value:sourceItm,
                            text: sIdx
                        });
                        select.append(option);
                    });

                    var td3 = $('<td/>');
                    td3.append(select);
                    tr.append(td3);

                    var delButton = $('a', {
                        'class': 'delete',
                        href: '#',
                        text: '<img title=\"Delete\" alt=\"Delete\" src=\"/html/themes/classic/images/common/delete.png\" class=\"icon\">'
                    });

                    var td4 = $('<td/>');
                    td4.append(delButton);
                    tr.append(td4);

                    contentTable.append(tr);

                });
                contentBox.empty().append(contentTable);
            },

            //When select changed set MessageSource value to input from selected option
            _selectOnChange: function(){
                var $select = $(this);
                $select
                    .closest('tr')
                    .children('td')
                    .eq(1)
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
                    .eq(2)
                    .children('select')
                    .children('option:selected');
                option
                    .val(inputVal)
                    .attr('data-value', 'changed');
            },

            //get all options that was changed, serialize them and post via ajax
            _saveResources:function () {
                var editor = this;
                var json = '[';
                var changedOptionArr = $('option[data-value=changed]');
                $.each(changedOptionArr, function(idx, option){
                    var $option = $(option);
                    json += '{"key":"' + $option.attr('data-key') + '","value":"' + $option.val() + '","locale":"' + $option.text() +'"},';
                });
                json = json.slice(0, -1);
                json += ']';
                Utils._sendAjax(editor.configuration.uploadResourcesURL, json, editor._afterResourcesUploaded)
            },

            //get current selected page and reload content using it
            _afterResourcesUploaded: function(response){
                var currentPage = $('.jPag-current').text();
                $globalScope._reloadContent(currentPage);
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
                $('.' + editor.saveButtonClass).on('click', Utils._createProxyListener(editor._saveResources, editor));
            },

            _saveResources: function(){
                var editor = this;
                var key = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.keyInputId));

                var isKeyValid = editor._validateKey(key);
                if(isKeyValid){
                    var json = editor._serializeTable();
                    var form = Utils._getObjById(Utils._getRealId(editor.configuration.namespace, editor.formId));
                    form.children("input[name=data]").val(json);
                    form.submit();
                }

            },

            _serializeTable: function(){
                var trArr = Utils._getObjById(Utils._getRealId($this.configuration.namespace, $this.contentId)).find('tr');
                var keyVal = Utils._getObjById(Utils._getRealId($this.configuration.namespace, $this.keyInputId)).val();
                var json = '[';
                $.each(trArr, function(idx, tr){
                    var $tr = $(tr);
                    json += '{"key":"' + keyVal +'","value":"' + $tr.children('td').eq(0).children('input[type=text]').val()
                        + '","locale":"' + $tr.children('td').eq(1).children('input[type=text]').val() + '"},';
                });
                json = json.slice(0, -1);
                json += ']';
                return json;
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

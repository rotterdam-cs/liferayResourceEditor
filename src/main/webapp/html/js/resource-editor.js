;
(function ($) {
    var ResourceEditor = function (config) {
        this.mswContentId = "mswContent";
        this.saveButtonClass = "save-button";
        this.newButtonClass = 'new-button';
        this.paginatorId = "paginator";
        $this = this;
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
                editor._viewPagination(editor.configuration.itemCount);

            },

            _bindEvents:function () {
                var editor = this;
                var contentBox = _getObjById(_getRealId(editor.configuration.namespace, editor.mswContentId));

                contentBox.delegate('select', 'change', editor._selectOnChange);

                contentBox.delegate('input.content', 'focusout', editor._inputOnFocusOut);

                contentBox.delegate('a.delete', 'click', editor._delete);

                $('.' + editor.saveButtonClass).on('click', _createProxyListener(editor._saveResources, editor));

                $('.' + editor.newButtonClass).on('click', editor._editResource);

            },

            _viewPagination:function(){

            },

            _editResource: function(){
                window.location.href = $this.configuration.editURL;
            },

            _delete: function(){
                if(confirm($this.configuration.deleteMSG)){
                    var $a = $(this);
                    var key = $a.closest('tr').children('td').eq(0).children('input[type=text]').val();
                    _sendAjax($this.configuration.deleteURL, key, $this._reloadContent);
                }
                return false;
            },

            _reloadContent: function(){
                var editor = $this;
                var json = _getJson(editor.configuration.resourceContentURL, editor._viewContent);
            },

            _loadContent: function(){
                var editor = $this;
                editor._viewContent(editor.configuration.mswJson);

            },

            _viewContent: function(json){

                var editor = $this;
                var contentBox = _getObjById(_getRealId(editor.configuration.namespace, editor.mswContentId));
                var content = '<table>';
                jQuery.each(json, function (idx, obj) {
                    var inputId = editor.configuration.namespace + '_resource_' + idx;
                    content += '<tr>';
                    content += '<td><input type="text" value="' + obj.key + '" readonly></td>';
                    content += '<td><input class="content" id="' + inputId + '" type="text" name="' + obj.key
                        + '" value="' + obj.source['sl_SI'] + '"></td>';
                    content += '<td><select name="' + obj.key + '">';
                    jQuery.each(obj.source, function (sIdx, sourceItm) {
                        content += '<option data-key="'+obj.key+'" data-value="" value="' + sourceItm + '">' + sIdx + '</option>';
                    });
                    content += '</select></td>';
                    content += '<td><a class="delete" href="#">'+'<img title=\"Delete\" alt=\"Delete\" src=\"/html/themes/classic/images/common/delete.png\" class=\"icon\">'+'</a></td>';
                    content += '</tr>';
                });
                content += '</table>';
                contentBox
                    .empty()
                    .append(content);
            },

            _selectOnChange: function(){
                var $select = $(this);
                $select
                    .closest('tr')
                    .children('td')
                    .eq(1)
                    .children('input[type=text]')
                    .val($select.val());
            },

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
                _sendAjax(editor.configuration.uploadResourcesURL, json, editor._afterResourcesUploaded)
            },

            _setResourceValue: function (){

            },

            _afterResourcesUploaded: function(response){
                console.log('success');
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
                $('.' + editor.saveButtonClass).on('click', _createProxyListener(editor._saveResources, editor));
            },

            _saveResources: function(){
                var editor = this;
                var key = _getObjById(_getRealId(editor.configuration.namespace, editor.keyInputId));

                var isKeyValid = editor._validateKey(key);
                if(isKeyValid){
                    var json = editor._serializeTable();
                    var form = _getObjById(_getRealId(editor.configuration.namespace, editor.formId));
                    console.log(json);
                    form.children("input[name=data]").val(json);
                    form.submit();
                }

            },

            _serializeTable: function(){
                var trArr = _getObjById(_getRealId($this.configuration.namespace, $this.contentId)).find('tr');
                var keyVal = _getObjById(_getRealId($this.configuration.namespace, $this.keyInputId)).val();
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

function _getRealId(namespase, id) {
    var realId = namespase + id;
    return realId;
}

function _getObjById(id) {
    return jQuery('#' + id);
}

function _sendAjax(url, data, success){
    jQuery.ajax({
        url: url,
        type: 'POST',
        data: 'data=' + data + '&rand=' +Math.random(),
        success: success
    });
}

function _getJson(url, success){
    jQuery.ajax({
        url: url,
        type: 'POST',
        dataType: 'json',
        success: success
    });
}

function _createProxyListener (listener, scope, args) {

    return function () {

        return listener.call(scope, args);

    }

}
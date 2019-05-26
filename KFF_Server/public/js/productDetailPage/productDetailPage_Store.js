$(document).ready(function()
    {
        /**
         * When a checkbox is checked the delete button will appear
         */
        $('.button_selectall').change(function(){
            if($(this).is(':checked')){
                $('.accreditationlist_table__Delete').prop('checked',true);
            }
            else {
                $('.accreditationlist_table__Delete').prop('checked',false);
            }
            makeappear();
        })


        $('.accreditationlist_table__Delete').change(function(){
            makeappear();
        })

        function makeappear(){
            var Ischeck = false;
            var Allcheck = false;
            var checkboxes = $('.accreditationlist_table__Delete:checked');
            if(checkboxes.length > 0){
                Ischeck = true;
                if(checkboxes.length == $('.accreditationlist_table__Delete').length){
                    Allcheck = true;
                }
            }
            if(Ischeck == true)
            {
                $('.deletebutton_deletemarked').css('visibility','visible');
            }
            else
            {
                $('.deletebutton_deletemarked').css('visibility','hidden');
            }
            if(Allcheck){
                $('.button_selectall').prop('checked',true);
            }
            else {
                $('.button_selectall').prop('checked',false);
            }
        }

        $('.deletebutton_deletemarked').on("click",function(e){
            var checkedboxes = $('.accreditationlist_table__Delete:checked');
            var confirmation = confirm('Do you want to delete these stores?');
            if(confirmation == true){
                var addids = [];
                for(var i = 0 ; i < checkedboxes.length; i++)
                {
                    addids.push(checkedboxes.get(i).getAttribute('value'));
                }
                var params = {
                    addids: addids,
                    productid : $(this).attr('value')
                };
                post('/detailproductPage_Store__Delete',params,"get");
            }
            else {
                e.preventDefault();
            }

        })

        /** Function to send new accreditation to the server
         * when the admin click the Add button in the modal
         */

        $(".modal-adding__Accreditation").on("click",function(event){
            var modalbody = $(this).parent().parent().children().eq(1);
            if(modalbody.children().eq(0).val().localeCompare("")==0 || modalbody.children().eq(1).val().localeCompare("")==0)
            {
                modalbody.children().eq(2).html("Please enter the Accreditation and the Rating you want to add to this brand");
            }
            else
            {
                params = {
                    accreditation: modalbody.children().eq(0).val(),
                    rating: modalbody.children().eq(1).val(),
                    productid: $(this).attr("value")
                }
                post('/detailproductPage_Accreditation__Insert',params,"get");
            }
        })

        /** Search box listener
         * When the admin type in the search box and press "enter"/ or click the Search button
         *
         */

        $('.pagebody_mainbody__infodisplay__searchinput').on("keypress",function(event){
            if(event.which == 13){
                var params = {
                    productid: $(this).attr('title'),
                    searchstring: $(this).val()
                }
                post('/detailproductPage_Store',params,"get");
            }
        })

        $('.pagebody_mainbody_infodisplay__searchsubmitbutton').on('click',function(event){
            var params = {
                productid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr('title'),
                searchstring: $('.pagebody_mainbody__infodisplay__searchinput').first().val()
            }
            post('/detailproductPage_Store',params,"get");
        })

        /**
         * paginationbutton listener
         */

        $('.pagination_button.page-item').on('click',function(event){
            var searchstring = $(this).attr('title');
            if($(this).children().eq(0).text().toLowerCase().localeCompare(("Next").toLowerCase()) == 0)
            {
                var page = parseInt($('.pagination>.active>a').text()) + 1;
                if(searchstring == null)
                {
                    var params = {
                        page : page,
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }
                else
                {
                    var params = {
                        page : page,
                        searchstring: searchstring,
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }
            }
            else if($(this).children().eq(0).text().toLowerCase().localeCompare(("Previous").toLowerCase()) == 0)
            {
                var page = parseInt($('.pagination>.active>a').text()) - 1;
                if(searchstring == null)
                {
                    var params = {
                        page : page,
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }
                else
                {
                    var params = {
                        page : page,
                        searchstring: searchstring,
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }
            }
            else
            {
                if(searchstring == null)
                {
                    var params = {
                        page : $(this).children().eq(0).text(),
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }
                else
                {
                    var params = {
                        page : $(this).children().eq(0).text(),
                        searchstring: searchstring,
                        productid: $('.modal-adding__Accreditation').first().attr("value")
                    }
                    post('/detailproductPage_Store',params,"get");
                }

            }
        })



        /** Post function
         *
         */
        function post(path, params, method) {
            method = method || "get"; // Set method to post by default if not specified.

            // The rest of this code assumes you are not using a library.
            // It can be made less wordy if you use one.
            var form = document.createElement("form");
            form.setAttribute("method", method);
            form.setAttribute("action", path);

            for(var key in params) {
                if(params.hasOwnProperty(key)) {
                    var hiddenField = document.createElement("input");
                    hiddenField.setAttribute("type", "hidden");
                    hiddenField.setAttribute("name", key);
                    hiddenField.setAttribute("value", params[key]);

                    form.appendChild(hiddenField);
                }
            }
            document.body.appendChild(form);
            form.submit();
        };

    }
)
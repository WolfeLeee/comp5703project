

$(document).ready(function()
{
    /* * * * * * * * * * * * * * * *
     * Sort function in the table  *
     * * * * * * * * * * * * * * * */

    $('.sorting').on("click",function(){
        var params = {
            sortquery: $(this).data('sort'),
            searchstring: $(this).data('search'),
            perPage: $(this).data('perpage')
        }
        post('/report',params,"get");
    })


    /* * * * * * * * * * * * * * * * *
     * Search function in the table  *
     * * * * * * * * * * * * * * * * */

    // enter will trigger the search function
    var searchInput = document.getElementById("searchInput");
    if(searchInput)
    {
        searchInput.addEventListener("keyup", function(event)
        {
            if(event.keyCode === 13)
            {
                event.preventDefault();
                // $("#searchButton").click();
            }
        });
    };
    /* * * * * * * * * * * * * * * * * * * *
     * Pagination to navigate through page *
     * * * * * * * * * * * * * * * * * * * */

    $('.page-link').click(function(){
        if(new String($(this).text().toLowerCase()).valueOf() == new String("Previous").valueOf())
        {
            var searchstring = $(this).data("search") || "";
            var params = {
                perPage: $('.custom-select').children("option:selected").val(),
                page: parseInt($('.active').first().text()) - 1,
                searchstring: searchstring,
                sortquery: $(this).data('sort')
            }
            post('/report',params,"get");
        }
        else if (new String($(this).text().toLowerCase()).valueOf() == new String("Next").valueOf())
        {
            var searchstring = $(this).data("search") || "";
            var params = {
                perPage: $('.custom-select').children("option:selected").val(),
                page: parseInt($('.active').first().text()) + 1,
                searchstring: searchstring,
                sortquery: $(this).data('sort')
            }
            post('/report',params,"get");
        }
        else
        {
            var searchstring = $(this).data("search") || "";
            var params = {
                perPage: $('.custom-select').children("option:selected").val(),
                page: parseInt($(this).text()),
                searchstring: searchstring,
                sortquery: $(this).data('sort')
            }
            post('/report',params,"get");
        }
    })

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Change number of products showed in a page function *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    $('.custom-select').on('change',function(){
        var params = {
            perPage: $(this).val(),
            searchstring: $(this).data("searchstring")
        }
        post('/report',params,"get");
    })

    /* * * * * * * * * * * * * * * * *
     * Search function in the table  *
     * * * * * * * * * * * * * * * * */

    $('#searchInput').keypress(function(e)
        {
            if(e.which == 13)
            {
                var params = {
                    searchstring: $(this).val()
                }
                post('/report',params,"get");
            }
        }
    )

    $('#searchButton').on("click",function(){
        var params = {
            searchstring: $('#searchInput').val()
        }
        post('/report',params,"get");
    })

    /* * * * * * * * * * * * * * * * *
     * Delete function in the table  *
     * * * * * * * * * * * * * * * * */

    $('.button_selectall').change(function(){
        if($(this).is(':checked')){
            $('.accreditationlist_table__Delete').prop('checked',true);
        }
        else {
            $('.accreditationlist_table__Delete').prop('checked',false);
        }
        makeappear();
    });


    $('.accreditationlist_table__Delete').change(function(){
        makeappear();
    });

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
    };

    $('.deletebutton_deletemarked').on("click",function(e){
        var checkedboxes = $('.accreditationlist_table__Delete:checked');
        var confirmation = confirm('Do you want to delete this(these) '+checkedboxes.length+' Report(s)?');
        if(confirmation == true){
            var rpids = [];
            for(var i = 0 ; i < checkedboxes.length; i++)
            {
                rpids.push(checkedboxes.get(i).getAttribute('value'));
            }
            var params = {
                rpids: rpids,
            };
            post('/report_Delete',params,"get");
        }
        else {
            e.preventDefault();
        }

    });

    /**
     * Add a report to the database
     */

    $('.addreport-button').on("click",function(){
        var trow = $(this).parent().parent();
        var brandId = $(trow).children().eq(0).data('brandid');
        var storeName = $(trow).children().eq(1).text();
        var storeAddress = $(trow).children().eq(2).data('streetaddress');
        var storePostCode = $(trow).children().eq(2).data('postcode');
        var storeState = $(trow).children().eq(2).data('state');
        $('.pagebody_inputform__storenameinput').text(storeName);
        $('.pagebody_inputform__storeaddressinput').text(storeAddress);
        $('.pagebody_inputform__storestateinput').text(storeState);
        $('.pagebody_inputform__storepostcodeinput').text(storePostCode);
        CompareStoreName(storeName,storePostCode,storeAddress,storeState,brandId)
        function CompareStoreName(storeName,storePostCode,storeAddress,storeState,brandId){
            var jqxhr = $.get("/GetAllStore")
                .done(function(data){
                    IdenticalStoreName(data,storeName,storePostCode,storeAddress,storeState,brandId);
                })
                .fail(function(jqXHR) {
                    console.log(jqXHR.status);
                })
        }

        function IdenticalStoreName(data,store_name,store_postcode,store_address,store_state,brandId) {
            var identicalstorename = false;
            var identicalstoreid = null;
            for (var i = 0; i < data.length; i++) {
                if (new String(store_name).toLowerCase().valueOf() == new String(data[i].storeName).toLowerCase().valueOf()) {
                    identicalstorename = true;
                    identicalstoreid = data[i]._id;
                }
            }
            var address = null;
            if (new String(store_state).valueOf() == "") {
                address = store_address + ", " + store_postcode;
            } else {
                address = store_address + ", " + store_state + ", " + store_postcode;
            }
            var jqxhr = $.get("/getLocation?address=" + address)
                .done(function (data) {
                    if (data.matched) {
                        StoreCallback(data, store_name, brandId,identicalstorename,identicalstoreid);
                    } else {
                        alert("Please enter a valid address;");
                    }
                })
                .fail(function (jqXHR) {
                    console.log(jqXHR.status);
                })
        }
        ///////////
        function StoreCallback(data,storeName,brandId,identicalStoreName,identicalStoreId) {

            $('.modal-body__store__addresslist').empty();
            $('.modal-header').attr('title', storeName);
            for (var i = 0; i < data.address.length; i++) {
                var modalbodystore = document.createElement("div");
                modalbodystore.className = 'modal-body__store';
                var storeaddress = "";
                if (data.address[i].hasOwnProperty('house_number')) {
                    storeaddress += data.address[i].house_number + " ";
                }
                if (data.address[i].hasOwnProperty('road')) {
                    storeaddress += data.address[i].road;
                } else if (data.address[i].hasOwnProperty('street')) {
                    storeaddress += data.address[i].street;
                }
                if (data.address[i].hasOwnProperty('suburb')) {
                    storeaddress += ", " + data.address[i].suburb;
                } else if (data.address[i].hasOwnProperty('city')) {
                    storeaddress += ", " + data.address[i].city;
                }

                var storepostcode = "" + data.address[i].postcode;
                var storestate = "" + data.address[i].state_code;
                var storelong = "" + data.geometry[i].lng;
                var storelat = "" + data.geometry[i].lat;
                var addressdiv = document.createElement("div");
                var postcodediv = document.createElement("div");
                var statediv = document.createElement("div");
                var longdiv = document.createElement("div");
                var latdiv = document.createElement("div");

                var chooseinput = createRadioElement("Choose this", false);
                chooseinput.className = 'modal-body__store__twenty';


                addressdiv.className = 'modal-body__store__eighty';
                addressdiv.className += ' modal-body__store__editable';
                addressdiv.title = storeaddress;
                $(addressdiv).data("second", "Address: ");
                addressdiv.innerHTML = "Address: " + storeaddress;


                postcodediv.className = 'modal-body__store__fifty';
                postcodediv.className += ' modal-body__store__editable';
                postcodediv.title = storepostcode;
                $(postcodediv).data("second", "Postcode: ");
                postcodediv.innerHTML = "Postcode: " + storepostcode;

                statediv.className = 'modal-body__store__fifty';
                statediv.className += ' modal-body__store__editable';
                statediv.title = storestate;
                $(statediv).data("second", "State: ");
                statediv.innerHTML = "State: " + storestate;

                longdiv.className = 'modal-body__store__fifty';
                longdiv.className += ' modal-body__store__editable';
                longdiv.title = storelong;
                $(longdiv).data("second", "Long: ");
                longdiv.innerHTML = "Long: " + storelong;

                latdiv.className = 'modal-body__store__fifty';
                latdiv.className += ' modal-body__store__editable';
                latdiv.title = storelat;
                $(latdiv).data("second", "Lat: ");
                latdiv.innerHTML = "Lat: " + storelat;

                modalbodystore.append(addressdiv);
                modalbodystore.append(chooseinput);
                modalbodystore.append(statediv);
                modalbodystore.append(postcodediv);
                modalbodystore.append(longdiv);
                modalbodystore.append(latdiv);
                $('.modal-body__store__addresslist').append(modalbodystore);

            }
            $('#exampleModalScrollable').modal("show");

            // Change information in the uploaded report///////////////

            $('.modal-body__store__changeable').dblclick(function () {
                if ($(this).find('.editBox2').length == 0) {
                    var editable = $(this).text();
                    var editbox = '<textarea class="editBox2">' + editable + '</textarea>';
                    $(this).html(editbox);
                }
                $('.modal-body__store__addresslist').empty();
                $('.initiallyhidden').css('visibility','visible');
            })

            $('.modal-body__store__changeable').on("keypress", function (event) {
                if (event.which == 13) {
                    var $text = $(this).find('.editBox2').first().val();
                    $(this).html($text);
                }
            })

            //////////// Search address after make any changes to the store//
            $('.pagebody_inputform__storesubmit').on('click',function(){
                var storeName =  $('.pagebody_inputform__storenameinput').first().text();
                var storePostCode =  $('.pagebody_inputform__storepostcodeinput').first().text();
                var storeAddress = $('.pagebody_inputform__storeaddressinput').first().text()
                var storeState = $('.pagebody_inputform__storestateinput').first().text();
                $(this).css('visibility','hidden');
                CompareStoreName(storeName,storePostCode,storeAddress,storeState,brandId)
            })

            ///////////

            $('.modal-body__store__twenty').on('click change', function () {
                $(".modal-body__store__twenty").parent().css('background-color', 'rgba(230,230,230,0.35)');
                $(".modal-body__store__twenty").parent().removeClass('modal-body__store__selected');
                $(".modal-body__store__twenty:checked").parent().css('background-color', 'rgba(120,120,255,0.35)');
                $(".modal-body__store__twenty:checked").parent().addClass('modal-body__store__selected');
            })

            $('.modal-body__store__editable').dblclick(function () {
                if ($(this).find('.editBox').length == 0) {
                    var editable = $(this).attr('title');
                    var editbox = '<textarea class="editBox">' + editable + '</textarea>';
                    $(this).html(editbox);
                }
            })

            $('.modal-body__store__editable').on("keypress", function (event) {
                if (event.which == 13) {
                    var $text = $(this).find('.editBox').first().val();
                    $(this).html($(this).data('second') + $text);
                    $(this).attr('title', $text);
                }
            })

            $('.modal-adding__Store').on('click', function (event) {
                if ($('.modal-body__store__selected').length == 0) {
                    $('.pagebody_inputform__errordialog__store').empty();
                    $('.pagebody_inputform__errordialog__store').append("<b>" + "Please select an address" + "</b><br>");
                }
                if ($('.editBox').length > 0) {
                    alert("Please finish editing the address first before adding a store;")
                }
                if ($('.modal-body__store__selected').length == 1 && $('.editBox').length == 0) {
                    var selectedbody = $('.modal-body__store__selected').first();
                    var params = {
                        name: $('.pagebody_inputform__storenameinput').first().text(),
                        brandid: brandId,
                        address: $(selectedbody).children().eq(0).attr('title'),
                        state: $(selectedbody).children().eq(2).attr('title'),
                        postcode: $(selectedbody).children().eq(3).attr('title'),
                        long: $(selectedbody).children().eq(4).attr('title'),
                        lat: $(selectedbody).children().eq(5).attr('title'),
                        identicalStoreName: identicalStoreName,
                        identicalStoreId: identicalStoreId
                    }
                    post("/report_Insert", params, "POST");
                }
            })


        }
    })

    function createRadioElement(name, checked) {
        var radioInput;
        try {
            var radioHtml = '<input type="radio"';
            if (checked) {
                radioHtml += ' checked="checked"';
            }
            radioHtml += '/>' + name;
            radioInput = document.createElement(radioHtml);
        } catch (err) {
            radioInput = document.createElement('input');
            radioInput.setAttribute('type', 'radio');
            radioInput.setAttribute('name', name);
            if (checked) {
                radioInput.setAttribute('checked', 'checked');
            }
        }
        return radioInput;
    }





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


});
$(document).ready(function()
    {
        /**
         * Function which makes "pagebody_inputform__brand" and "pagebody_inputform__store appear
         * after changing the select element "inputform_choosedata__data"
         */
        $('#inputform_choosedata__data').on('change',function(){
            if($(this).val().localeCompare("Brand")==0)
            {
                $(this).parent().css("display","none");
                $('.pagebody_inputform__brand').css("display","block");
                $('.pagebody_mainbody__sectionheader__brand').html("Import/ Insert Brand Data");
            }
            else if ($(this).val().localeCompare("Store")==0)
            {
                $(this).parent().css("display","none");
                $('.pagebody_inputform__store').css("display","block");
                $('.pagebody_mainbody__sectionheader__brand').html("Import/ Insert Store Data");
            }
        })

        /**
         * Function to check identical brand name
         */
        var branddata = null;

        $('.pagebody_inputform__brandsubmit').on("click",function(event)
        {
            var brand_name = $('.pagebody_inputform__brandnameinput').first().val();

            var jqxhr = $.get( "/GetAllBrand")
                .done(function(data){
                        BrandCallback(data);
                    }
                )
                .fail(function(jqXHR) {
                    console.log(jqXHR.status);
                })
        })
        /** Callback function to check whether the name and category of the new brand are identical to any brands in the database;
         * Furthermore, the function also checks whether all required input boxes are filled.
         * In case all of the requirements are satisfied, the function will send the query and files to the server.
         * Hence, the server will record new brand to the dabase.
         */
        function BrandCallback(data)
        {
            branddata = data;

            var brand_name = $('.pagebody_inputform__brandnameinput').first().val();
            var brand_category = $('.pagebody_inputform__brandcategoryinput').first().val();
            var brand_accreditation = $('.pagebody_inputform__brandaccreditationinput').first().val();
            var brand_rating = $('.pagebody_inputform__brandratinginput').first().val();

            var identicalname = false;
            for(var i = 0 ; i < branddata.length ; i ++)
            {
                if(new String(branddata[i].Brand_Name.toLowerCase()).valueOf() == new String(brand_name.toLowerCase()).valueOf() && new String(branddata[i].Category.toLowerCase()).valueOf() == new String(brand_category.toLowerCase()).valueOf() )
                {
                    identicalname = true;
                }
            }
            if(new String("").valueOf() == new String(brand_name).valueOf() || new String("").valueOf() == new String(brand_category).valueOf() || new String("").valueOf() == new String(brand_accreditation).valueOf() || new String("").valueOf() == new String(brand_rating).valueOf())
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__errordialog').append("<b>"+"Please enter all the required information;"+"</b><br>");
                if(identicalname)
                {
                    $('.pagebody_inputform__errordialog').append("<b>"+"This name and category have already been used;"+"</b><br>");
                }
            }
            else if (identicalname)
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__errordialog').append("<b>"+"This name has already been used;"+"</b><br>");
            }
            else
            {
                $('.pagebody_inputform__errordialog').empty();
                $('.pagebody_inputform__inputnewbrand').children().eq(0).submit();
            }
        }



        /**
         *
         */

        $('.pagebody_inputform__storesubmit').on("click",function(event)
        {
            var store_name = $('.pagebody_inputform__storenameinput').first().val();
            var store_state = $('.pagebody_inputform__storestateinput').first().val();
            var store_postcode = $('.pagebody_inputform__storepostcodeinput').first().val();
            var store_address = $('.pagebody_inputform__storeaddressinput').first().val();

            if(new String(store_name).valueOf() == "" || new String(store_postcode).valueOf() == "" || new String(store_address).valueOf() == "")
            {
                $('.pagebody_inputform__errordialog__store').empty();
                $('.pagebody_inputform__errordialog__store').append("<b>"+"Please enter all the required information;"+"</b><br>");
            }
            else
            {
                var address = null;
                if(new String(store_state).valueOf() == "")
                {
                    address = store_address+", "+store_postcode;
                }
                else
                {
                    address = store_address+", "+store_state+", "+store_postcode;
                }
                var jqxhr = $.get("/getLocation?address="+address)
                    .done(function(data){
                        if(data.matched)
                        {
                            StoreCallback(data);
                        }
                        else
                        {
                            $('.pagebody_inputform__errordialog__store').empty();
                            $('.pagebody_inputform__errordialog__store').append("<b>"+"Please enter a valid address;"+"</b><br>");
                        }
                    })
                    .fail(function(jqXHR) {
                        console.log(jqXHR.status);
                    })
            }

            /**
             *
             */
            function StoreCallback(data) {
                $('.modal-body').empty();
                $('.modal-header').attr('title',$('.pagebody_inputform__storenameinput').first().val());
                for( var i = 0 ; i < data.address.length ; i++)
                {
                    var modalbodystore = document.createElement("div");
                    modalbodystore.className = 'modal-body__store';
                    var storeaddress = "";
                    if(data.address[i].hasOwnProperty('house_number'))
                    {
                        storeaddress += data.address[i].house_number +" ";
                    }
                    if(data.address[i].hasOwnProperty('road'))
                    {
                        storeaddress += data.address[i].road;
                    }
                    else if(data.address[i].hasOwnProperty('street'))
                    {
                        storeaddress += data.address[i].street;
                    }
                    if(data.address[i].hasOwnProperty('suburb'))
                    {
                        storeaddress += ", "+data.address[i].suburb;
                    }
                    else if(data.address[i].hasOwnProperty('city'))
                    {
                        storeaddress += ", "+data.address[i].city;
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

                    var chooseinput = createRadioElement("Choose this",false);
                    chooseinput.className = 'modal-body__store__twenty';


                    addressdiv.className = 'modal-body__store__eighty';
                    addressdiv.className += ' modal-body__store__editable';
                    addressdiv.title=storeaddress;
                    $(addressdiv).data("second","Address: ");
                    addressdiv.innerHTML = "Address: " + storeaddress;

                    postcodediv.className = 'modal-body__store__fifty';
                    postcodediv.className += ' modal-body__store__editable';
                    postcodediv.title=storepostcode;
                    $(postcodediv).data("second","Postcode: ");
                    postcodediv.innerHTML = "Postcode: "+ storepostcode;

                    statediv.className = 'modal-body__store__fifty';
                    statediv.className += ' modal-body__store__editable';
                    statediv.title=storestate;
                    $(statediv).data("second","State: ");
                    statediv.innerHTML = "State: " + storestate;

                    longdiv.className = 'modal-body__store__fifty';
                    longdiv.className += ' modal-body__store__editable';
                    longdiv.title=storelong;
                    $(longdiv).data("second","Long: ");
                    longdiv.innerHTML = "Long: "+ storelong;

                    latdiv.className = 'modal-body__store__fifty';
                    latdiv.className += ' modal-body__store__editable';
                    latdiv.title=storelat;
                    $(latdiv).data("second","Lat: ");
                    longdiv.innerHTML = "Lat: "+ storelat;

                    modalbodystore.append(addressdiv);
                    modalbodystore.append(chooseinput);
                    modalbodystore.append(statediv);
                    modalbodystore.append(postcodediv);
                    modalbodystore.append(longdiv);
                    modalbodystore.append(latdiv);
                    $('.modal-body').append(modalbodystore);

                }
                $('#exampleModalScrollable').modal("show");
                $('.modal-body__store__twenty').on('click change',function(){
                    $(".modal-body__store__twenty").parent().css('background-color','rgba(230,230,230,0.35)');
                    $(".modal-body__store__twenty").parent().removeClass('modal-body__store__selected');
                    $(".modal-body__store__twenty:checked").parent().css('background-color','rgba(120,120,255,0.35)');
                    $(".modal-body__store__twenty:checked").parent().addClass('modal-body__store__selected');
                })

                $('.modal-body__store__editable').dblclick(function(){
                    if($(this).find('.editBox').length == 0){
                        var editable = $(this).attr('title');
                        var editbox = '<textarea class="editBox">' + editable + '</textarea>';
                        $(this).html(editbox);
                    }
                })

                $('.modal-body__store__editable').on("keypress", function(event){
                    if(event.which == 13){
                        var $text = $(this).find('.editBox').first().val();
                        $(this).html($(this).data('second')+$text);
                        $(this).attr('title',$text);
                    }
                })

                $('.modal-adding__Store').on('click',function(event){
                    if($('.modal-body__store__selected').length == 0)
                    {
                        alert("Please select an address.")
                    }
                    if($('.editBox').length > 0)
                    {
                        alert("Please finish editing the address first before adding a store;")
                    }
                    if($('.modal-body__store__selected').length == 1 && $('.editBox').length == 0 )
                    {
                        var selectedbody = $('.modal-body__store__selected').first();
                        var params = {
                            address: $(selectedbody).children().eq(0).attr('title'),
                            state: $(selectedbody).children().eq(2).attr('title'),
                            postcode: $(selectedbody).children().eq(3).attr('title'),
                            long: $(selectedbody).children().eq(4).attr('title'),
                            lat: $(selectedbody).children().eq(5).attr('title'),
                            name: $('.modal-header').attr('title')
                        }

                        post("/insertnewStore",params,"POST");
                    }
                })


            }
            function createRadioElement( name, checked ) {
                var radioInput;
                try {
                    var radioHtml = '<input type="radio"';
                    if ( checked ) {
                        radioHtml += ' checked="checked"';
                    }
                    radioHtml += '/>'+name;
                    radioInput = document.createElement(radioHtml);
                } catch( err ) {
                    radioInput = document.createElement('input');
                    radioInput.setAttribute('type', 'radio');
                    radioInput.setAttribute('name', name);
                    if ( checked ) {
                        radioInput.setAttribute('checked', 'checked');
                    }
                }
                return radioInput;}

            /**
             *
             */


        })

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



    })
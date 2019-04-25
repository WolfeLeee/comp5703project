$(document).ready(function() {
        $('.modal-body__search').on('input', function () {
            var brandnotinstore = $('.modal-body__store__brandnotinstorelist');
            for (var i = 0; i < brandnotinstore.length; i++) {
                if ($(brandnotinstore[i]).children().eq(0).text().toLowerCase().includes($(this).val().toLowerCase())) {
                    $(brandnotinstore[i]).css('display', "block");
                } else {
                    $(brandnotinstore[i]).css('display', "none");
                }
            }
        })

        $('.modal-body__store__insertbrandtostore').on("click", function () {
            var brandid = $(this).data('brandid');
            var storeid = $(this).data('storeid');
            var jqxhr = $.get("/detailstorePage_Brand__Insert?brandid=" + brandid + "&storeid=" + storeid)
                .done(function (data) {
                    alert(data.matched);
                })
                .fail(function (jqXHR) {
                    console.log(jqXHR.status);
                })
        })
        /**
         * When a checkbox is checked the delete button will appear
         */
        $('.button_selectall').change(function () {
            if ($(this).is(':checked')) {
                $('.accreditationlist_table__Delete').prop('checked', true);
            } else {
                $('.accreditationlist_table__Delete').prop('checked', false);
            }
            makeappear();
        })


        $('.accreditationlist_table__Delete').change(function () {
            makeappear();
        })

        function makeappear() {
            var Ischeck = false;
            var Allcheck = false;
            var checkboxes = $('.accreditationlist_table__Delete:checked');
            if (checkboxes.length > 0) {
                Ischeck = true;
                if (checkboxes.length == $('.accreditationlist_table__Delete').length) {
                    Allcheck = true;
                }
            }
            if (Ischeck == true) {
                $('.deletebutton_deletemarked').css('visibility', 'visible');
            } else {
                $('.deletebutton_deletemarked').css('visibility', 'hidden');
            }
            if (Allcheck) {
                $('.button_selectall').prop('checked', true);
            } else {
                $('.button_selectall').prop('checked', false);
            }
        }

        $('.deletebutton_deletemarked').on("click", function (e) {
            var checkedboxes = $('.accreditationlist_table__Delete:checked');
            var confirmation = confirm('Do you want to delete this(these) ' + checkedboxes.length + ' address(es) from the store?');
            if (confirmation == true) {
                var addids = [];
                for (var i = 0; i < checkedboxes.length; i++) {
                    addids.push(checkedboxes.get(i).getAttribute('value'));
                }
                var params = {
                    addids: addids,
                    storeid: $(this).attr('value')
                };
                post('/detailstorePage_Address__Delete', params, "get");
            } else {
                e.preventDefault();
            }

        })


        /** Search box listener
         * When the admin type in the search box and press "enter"/ or click the Search button
         *
         */

        $('.pagebody_mainbody__infodisplay__searchinput').on("keypress", function (event) {
            if (event.which == 13) {
                var params = {
                    storeid: $(this).attr('title'),
                    searchstring: $(this).val()
                }
                post('/detailstorePage_Address', params, "get");
            }
        })

        $('.pagebody_mainbody_infodisplay__searchsubmitbutton').on('click', function (event) {
            var params = {
                storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr('title'),
                searchstring: $('.pagebody_mainbody__infodisplay__searchinput').first().val()
            }
            post('/detailstorePage_Address', params, "get");
        })

        /**
         * paginationbutton listener
         */

        $('.pagination_button.page-item').on('click', function (event) {
            var searchstring = $(this).attr('title');
            if ($(this).children().eq(0).text().toLowerCase().localeCompare(("Next").toLowerCase()) == 0) {
                var page = parseInt($('.pagination>.active>a').text()) + 1;
                if (searchstring == null) {
                    var params = {
                        page: page,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
                } else {
                    var params = {
                        page: page,
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
                }
            } else if ($(this).children().eq(0).text().toLowerCase().localeCompare(("Previous").toLowerCase()) == 0) {
                var page = parseInt($('.pagination>.active>a').text()) - 1;
                if (searchstring == null) {
                    var params = {
                        page: page,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
                } else {
                    var params = {
                        page: page,
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
                }
            } else {
                if (searchstring == null) {
                    var params = {
                        page: $(this).children().eq(0).text(),
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
                } else {
                    var params = {
                        page: $(this).children().eq(0).text(),
                        searchstring: searchstring,
                        storeid: $('.pagebody_mainbody__infodisplay__searchinput').first().attr("title")
                    }
                    post('/detailstorePage_Address', params, "get");
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

            for (var key in params) {
                if (params.hasOwnProperty(key)) {
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

        /**
         * Function to insert new address to the store
         */

        /**
         *
         */

        $('.pagebody_inputform__storesubmit').on("click", function (event) {
            var store_name = $('.pagebody_inputform__storenameinput').first().val();
            var store_state = $('.pagebody_inputform__storestateinput').first().val();
            var store_postcode = $('.pagebody_inputform__storepostcodeinput').first().val();
            var store_address = $('.pagebody_inputform__storeaddressinput').first().val();

            if (new String(store_name).valueOf() == "" || new String(store_postcode).valueOf() == "" || new String(store_address).valueOf() == "") {
                $('.pagebody_inputform__errordialog__store').empty();
                $('.pagebody_inputform__errordialog__store').append("<b>" + "Please enter all the required information;" + "</b><br>");
            } else {
                $('.pagebody_inputform__errordialog__store').empty();
                var address = null;
                if (new String(store_state).valueOf() == "") {
                    address = store_address + ", " + store_postcode;
                } else {
                    address = store_address + ", " + store_state + ", " + store_postcode;
                }

                var jqxhr = $.get("/getLocation?address=" + address)
                    .done(function (data) {
                        if (data.matched) {
                            StoreCallback(data);
                        } else {
                            $('.pagebody_inputform__errordialog__store').empty();
                            $('.pagebody_inputform__errordialog__store').append("<b>" + "Please enter a valid address;" + "</b><br>");
                        }
                    })
                    .fail(function (jqXHR) {
                        console.log(jqXHR.status);
                    })
            }


            /**
             *
             */
            function StoreCallback(data) {
                $('.modal-body__store__addresslist').empty();
                $('.modal-header').attr('title', $('.pagebody_inputform__storenameinput').first().val());
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
                        alert("Please select an address.")
                    }
                    if ($('.editBox').length > 0) {
                        alert("Please finish editing the address first before adding a store;")
                    }
                    if ($('.modal-body__store__selected').length == 1 && $('.editBox').length == 0) {
                        var selectedbody = $('.modal-body__store__selected').first();
                        var params = {
                            address: $(selectedbody).children().eq(0).attr('title'),
                            state: $(selectedbody).children().eq(2).attr('title'),
                            postcode: $(selectedbody).children().eq(3).attr('title'),
                            long: $(selectedbody).children().eq(4).attr('title'),
                            lat: $(selectedbody).children().eq(5).attr('title'),
                            storeid: $('.modal-header').first().data('storeid')
                        }
                        var jqxhr = $.get("/CompareStoreAddress?storeid=" + params.storeid+"&address="+params.address+"&postcode="+params.postcode)
                                .done(function (data) {
                                    if(data.matched)
                                    {
                                        post("/detailstorePage_Address__Insert", params, "POST");
                                    }
                                    else
                                    {
                                        $('.pagebody_inputform__errordialog__store').empty();
                                        $('.pagebody_inputform__errordialog__store').append("<b>" + data.alert + "</b><br>");
                                    }
                                })
                                .fail(function (jqXHR) {
                                    console.log(jqXHR.status);
                                })
                    }
                })


            }

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

            /**
             *
             */


        })
    }
)
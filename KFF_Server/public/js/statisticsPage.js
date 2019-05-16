
$(document).ready(function()
{
    /***********************
     * Radiobutton onclick listener. Whenever the admin clicks on a radio button, the other will be disabled
     ***********************/
    $('.pagebody_inputform__radiobutton').unbind().click(function()
    {
        $('.pagebody_inputform__radiobutton').next().find('*').css('visibility','hidden');
        $(this).next().find('*').css('visibility','visible');
    })

    /***********************
     *Generate csv file function;
     * As the admin click on this button, the front-end will send an ajax request to the server,
     * and receive a json data
     ***********************/

    $('.pagebody_inputform__generatebrandcsvstatistics').unbind().click(function()
    {
        var Timeline = $('.pagebody_inputform__radiobutton:checked').next().text();
        var Brand = $('.pagebody_inputform__statisticsbrand').first().val();
        var Gender = $('.pagebody_inputform__statisticsgender').first().val();
        if(new String(Timeline).toLowerCase().valueOf() == new String("All").toLowerCase().valueOf())
        {
            if(new String(Brand).toLowerCase().valueOf() == "" )
            {
                alert("Please enter the brand you want to get information");
            }
            else
            {
                var params = {
                    Timeline: Timeline,
                    Brand: Brand,
                    Gender: Gender
                }
                var jqxhr = $.get( "/GenerateStatistics",params)
                    .done(function(data){
                            const csvfile = data.map(row => ({
                                "Brand":row.brandName,
                                "Gender":row.gender,
                                "Age":row.age,
                                "Timeline":row.date,
                                "Count":row.count
                            }))
                            const csvData = objectToCsv(csvfile);
                            downloadcsv(csvData);
                        }
                    )
                    .fail(function(jqXHR) {
                        console.log(jqXHR.status);
                    })
            }
        }
        else if (new String(Timeline).toLowerCase().valueOf() == new String("Period").toLowerCase().valueOf())
        {
            if($('#pagebody_inputform__startdate').val() == "" || $('#pagebody_inputform__startdate').val() == ""){
                alert("Please enter the start date and end date");
            }
            else
            {
                if(new String(Brand).toLowerCase().valueOf() == "" )
                {
                    alert("Please enter the brand you want to get information");
                }
                else
                {
                    var params = {
                        Timeline: Timeline,
                        Brand: Brand,
                        Gender: Gender,
                        Startdate: $('#pagebody_inputform__startdate').val(),
                        Enddate: $('#pagebody_inputform__enddate').val()
                    }
                    var jqxhr = $.get( "/GenerateStatistics",params)
                        .done(function(data){
                                const csvfile = data.map(row => ({
                                    "Brand":row.brandName,
                                    "Gender":row.gender,
                                    "Age":row.age,
                                    "Timeline":row.date,
                                    "Count":row.count
                                }))
                                const csvData = objectToCsv(csvfile);
                                downloadcsv(csvData);
                            }
                        )
                        .fail(function(jqXHR) {
                            console.log(jqXHR.status);
                        })
                }

            }

        }


    })

    /***********************
     * Function to process data into csv rows
     ***********************/

    const objectToCsv = function(data)
    {
        const csvRows = [];

        // Get the headers

        const headers = Object.keys(data[0]);
        csvRows.push(headers.join(','));

        // Loop over the rows
        for(const row of data)
        {
            const values = headers.map(header => {
                const escaped = (''+row[header]).replace(/"/g,'\\"');
                return `"${escaped}"`;
            });
            csvRows.push(values.join(','));
        }
        return csvRows.join('\n');
    }

    /***********************
     * Send csv file to the client-side
     ***********************/

    const downloadcsv = function(data)
    {
        const blob = new Blob([data],{type:'text/csv'});
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.setAttribute('hidden','');
        a.setAttribute('href',url);
        a.setAttribute('download','StatisticalReport.csv');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }




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
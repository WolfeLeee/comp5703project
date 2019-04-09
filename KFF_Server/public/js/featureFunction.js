google.charts.load('current', {packages: ['corechart']});
//google.charts.setOnLoadCallback(drawChart);

var numTitles = 3;

var dataOverallBar, dataOverallPie;

// window.onload = function()
// {
//
// };

function checkInput(num)
{
    var input = num;
    var reg = /^[0-9]+$/;
    if(!input.trim().match(reg))
    {
        alert("Only integer numbers!");
        return false;
    }
    // if(!((typeof input === 'number') && (input % 1 === 0)))
    // {
    //     alert("Only integer numbers!");
    //     return false;
    // }
    return true;
}

function drawOverallBar()
{
    // adjust the data from ajax call
    var columns = Object.keys(dataOverallBar[0]);
    var data = dataOverallBar.map(function (result)
    {
        var tableRow = [];
        columns.forEach(function (col)
        {
            tableRow.push(result[col]);
        });
        return tableRow;
    });
    console.log(data);

    // automatically adding columns with default names by loop
    // columns.forEach(function (columnName)
    // {
    //     graphData.addColumn('number', columnName);
    // });

    // set up all columns and rows
    graphData = new google.visualization.DataTable();
    graphData.addColumn('string', 'Year');
    graphData.addColumn('number', 'Administrator');
    graphData.addColumn('number', 'Anonymous');
    graphData.addColumn('number', 'Bot');
    graphData.addColumn('number', 'Regular user');
    graphData.addRows(data);
    // set up options for the chart
    var options = {
        'title': "Overall yearly revision number distribution",
        'width': 1300,
        'height': 600,
        legend: {position: 'top', alignment: 'center'},
        animation: {
            duration: 1500,
            easing: "out",
            startup: true,
        },
        bar: {
            groupWidth: 40  // Set the width for each bar
        },
        hAxis: {
            title: 'Year'
        },
        vAxis: {
            title: 'Revisions',
            format: 'decimal'
        },
        annotations: {
            boxStyle: {
                stroke: 8
            }
        },
        series: {
            0: {color: '#ffbd87'},
            1: {color: '#ff8fac'},
            2: {color: '#6fc0ed'},
            3: {color: '#ba92fa'},
        }
    };
    // the position to draw out the chart
    var chart = new google.visualization.ColumnChart($("#outputChart")[0]);
    // execute the draw function
    chart.draw(graphData, options);
}
function drawOverallPie()
{
    // adjust the data from ajax call
    var columns = Object.keys(dataOverallPie[0]);
    var data = dataOverallPie.map(function (result)
    {
        var tableRow = [];
        columns.forEach(function (col)
        {
            tableRow.push(result[col]);
        });
        return tableRow;
    });
    console.log(data);

    // set up all columns and rows
    graphData = new google.visualization.DataTable();
    graphData.addColumn('string', 'UserType');
    graphData.addColumn('number', 'Revisions');
    graphData.addRows(data);
    // set up options for the chart
    var options = {
        'title': "Overall revision number distribution by user type",
        'width': 800,
        'height': 800,
        legend: {position: 'top', alignment: 'center'},
        is3D: true,
        colors: ['#ff5682', '#00a4ee', '#ffcc44', '#dcdcdc']
    };
    // the position to draw out the chart
    var chart = new google.visualization.PieChart($("#outputChart")[0]);
    // execute the draw function
    chart.draw(graphData, options);
}

$(document).ready(function()
{
    // get the data for overall feature graphs
    $.getJSON('/data/overall-graph', null, function(resData)
    {
        dataOverallBar = resData.dataBar;
        dataOverallPie = resData.dataPie;
        console.log("Overall Data:");
        console.log(dataOverallBar);
        console.log(dataOverallPie);
    });

    // print out articles as default
    var selector1 = $("#articleHighestRev ol");
    var selector2 = $("#articleLowestRev ol");
    var index = res1.length - 1;

    var selector3 = $("#articleEditByLargeReUser");
    var selector4 = $("#articleEditBySmallReUser");

    var selector5 = $("#articleLongHistory ol");
    var selector6 = $("#articleShortHistory ol");

    // set up all result needed from the page first
    for (var i = 0; i < numTitles; i++)
    {
        var element1 = document.createElement("li");
        element1.innerHTML = res1[i]._id + ": " + res1[i].totalRev;
        selector1.append(element1);

        var element3 = document.createElement("li");
        element3.innerHTML = res3[i]._id + ": " + res3[i].timestamp;
        selector5.append(element3);
    }
    for (var i = 0; i < numTitles; i++)
    {
        var element2 = document.createElement("li");
        element2.innerHTML = res1[index]._id + ": " + res1[index].totalRev;
        selector2.append(element2);
        index--;

        var element4 = document.createElement("li");
        element4.innerHTML = res4[i]._id + ": " + res4[i].timestamp;
        selector6.append(element4);
    }
    selector3.html(res2[0]._id + ": " + res2[0].userCount + " unique registered users");
    selector4.html(res2[res2.length - 1]._id + ": " + res2[res2.length - 1].userCount + " unique registered users");

    // when user gives a number and press button then change the number of articles shown
    $("#inputBtn").click(function(event)
    {
        event.preventDefault();
        var input = $("#inputNum");
        var num = input.val();
        input.val("");
        //console.log(num);
        if(checkInput(num))
        {
            numTitles = parseInt(num);
            if(numTitles <= res1.length && numTitles > 0)
            {
                // reset the original ones first
                selector1.html("");
                selector2.html("");
                index = res1.length - 1;
                // print out the news
                for (var i = 0; i < numTitles; i++)
                {
                    var element = document.createElement("li");
                    element.innerHTML = res1[i]._id + ": " + res1[i].totalRev;
                    selector1.append(element);
                }
                for (var i = 0; i < numTitles; i++)
                {
                    var element = document.createElement("li");
                    element.innerHTML = res1[index]._id + ": " + res1[index].totalRev;
                    selector2.append(element);
                    index--;
                }
            }
            else if(numTitles <= 0)
            {
                alert("The number cannot be smaller or equal to 0!");
            }
            else
            {
                alert("The number has exceeded the length!");
            }
        }
    });

    // click buttons to output charts
    $("#overall_barchart_button").click(function(event)
    {
        if(!dataOverallBar)
            return;
        event.preventDefault();
        drawOverallBar();
    });
    $("#overall_piechart_button").click(function(event)
    {
        if(!dataOverallPie)
            return;
        event.preventDefault();
        drawOverallPie();
    });

    // get the author data and find the author then output table
    $("#searchAuthor").click(function(event)
    {
        event.preventDefault();
        $(".author_table_block").css("display", "block");
        var inputAuthorSelector = $("#input_author");
        var authorName = inputAuthorSelector.val();
        inputAuthorSelector.val("");
        $.post('/data/author-data', {name: authorName}, function(resData)
        {
            console.log("Author Data:");
            console.log(resData);
            var tbody = document.getElementById("author_search");
            tbody.innerHTML = "";
            var tr, td;
            //var tbody = document.getElementById("author_search");
            for(var i = 0; i < resData.length; i++)
            {
                tr = tbody.insertRow(tbody.rows.length);

                td =  tr.insertCell(tr.cells.length);
                td.innerHTML = resData[i].title;

                td = tr.insertCell(tr.cells.length);
                td.innerHTML = resData[i].author;

                td = tr.insertCell(tr.cells.length);
                td.innerHTML = resData[i].revisionNum;

                td = tr.insertCell(tr.cells.length);
                var timeStamp = JSON.stringify(resData[i].time).replace('[', '');
                timeStamp = timeStamp.replace(']', '');
                timeStamp = timeStamp.replace(',', ', ');
                td.setAttribute("class", "author_timestamp");
                td.innerHTML = timeStamp;
            }
            $(".author_timestamp").css("display", "none");
        });
    });
    $("#viewTime").click(function(event)
    {
        event.preventDefault();
        $(".author_timestamp").css("display", "block");
    });

    // switch the three different features
    $("#overall_button").click(function(event)
    {
        event.preventDefault();
        $(".overall_feature").css("display", "block");
        $(".individual_feature").css("display", "none");
        $(".author_feature").css("display", "none");
    });
    $("#individual_button").click(function(event)
    {
        event.preventDefault();
        $(".overall_feature").css("display", "none");
        $(".individual_feature").css("display", "block");
        $(".author_feature").css("display", "none");
    });
    $("#author_button").click(function(event)
    {
        event.preventDefault();
        $(".overall_feature").css("display", "none");
        $(".individual_feature").css("display", "none");
        $(".author_feature").css("display", "block");
    });
});


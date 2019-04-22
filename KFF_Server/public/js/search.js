

$(document).ready(function()
{
	/* * * * * * * * * * * * * * * *
	 * Searching function in table *
	 * * * * * * * * * * * * * * * */

	// search button listener
	$("#searchButton").click(function(event)
	{
		event.preventDefault();

		// get the text from search
		var searchText = $("#searchInput").val().toString().toLowerCase();
		// $("#searchInput").val("");
		// console.log(displayAllData.length);

		// check if match to the search text with product data
		// and display the matched data in the table
		var tbody = document.getElementById("table_body");
		tbody.innerHTML = "";  // empty the original table
		var tr, td;
		var numOfRows = 0;
		var numOfSearchProducts = 0;
		for(var i = 0; i < displayAllData.length; i++)
		{
			var brandName = displayAllData[i].Brand_Name.toString().toLowerCase();
			if(brandName.startsWith(searchText))
			{
				tr = tbody.insertRow(numOfRows++);

				// brand name
				td = tr.insertCell(0);
				td.setAttribute("class", "Table_Brand_Name");
				td.innerHTML = "<a href='/detailproductPage?productid="+displayAllData[i]._id+"'>" + displayAllData[i].Brand_Name + "</a>";

				// accreditation
				td = tr.insertCell(1);
				for(var j = 0; j < displayAllData[i].Accreditation.length; j++)
				{
					td.innerHTML += "<div>" + displayAllData[i].Accreditation[j].Accreditation + " - " + displayAllData[i].Accreditation[j].Rating + "</div>";
				}

				// category
				td = tr.insertCell(2);
				td.innerHTML = displayAllData[i].Category;

				// delete checkbox
				td = tr.insertCell(3);
				td.setAttribute("class", "deletecheckbox");
				td.innerHTML = "<input type='checkbox' value='"+displayAllData[i]._id+"'>";

				// count the number of searched products
				numOfSearchProducts++;
			}
		}

		// change the number of display products
		document.getElementById("datable_info").innerHTML = "Showing " + numOfSearchProducts + " of " + displayAllData.length;
	});
	// enter will trigger the search button as well
	var searchInput = document.getElementById("searchInput");
	if(searchInput)
	{
		searchInput.addEventListener("keyup", function(event)
		{
			if(event.keyCode === 13)
			{
				event.preventDefault();
				$("#searchButton").click();
			}
		});
	}

	/* * * * * * * * * * * * * * * * *
	 * Displaying function in table  *
	 * * * * * * * * * * * * * * * * */

	$("select.custom-select").change(function()
	{
		// var selectedNumber = $(this).children("option:selected").text();
		location.href = $(this).val();
	});
});
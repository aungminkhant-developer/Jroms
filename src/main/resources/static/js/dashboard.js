
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
var allCandidate = document.getElementById('allCandidate');
var passedCount = document.getElementById('passedCount');
var failedCount = document.getElementById('failedCount');
var pendingCount = document.getElementById('pendingCount');
var cancelCount = document.getElementById('cancelCount');
var nextCount = document.getElementById('nextCount');
var jobAccepCount=document.getElementById('jobAcceptCount');
var notReachCount=document.getElementById('notReachCount');

'use strict';

let cardColor, headingColor, axisColor, shadeColor, borderColor;

cardColor = config.colors.white;
headingColor = config.colors.headingColor;
axisColor = config.colors.axisColor;
borderColor = config.colors.borderColor;

//candidate statistics chart
var statisticsList = [passedList, failedList, pendingList, cancelList, nextInterviewList,jobOfferedList,notReachList]
const candidateStatistics = document.querySelector('#candidateStatisticsChart');
if (typeof candidateStatistics !== 'undefined' && candidateStatistics !== null) {
  const originalSeries = statisticsList;
  const total = originalSeries.reduce((sum, value) => sum + value, 0);

  const percentages = originalSeries.map(value => parseFloat((value / total * 100).toFixed(2)));

  const candidateChartConfig = {
    chart: {
      type: 'donut',
    },
    labels: ['Passed', 'Failed', 'Pending', 'Cancel', 'Next Interview','Job Offered','Not Reach'],
    series: percentages,
    colors: [
      config.colors.success,
      '#f0510d',
      config.colors.secondary,
      '#ffc107',
      '#820df0',
      config.colors.primary,
      '#0dcaf0'
    ],
    plotOptions: {
      pie: {
        donut: {
          size: '75%',
          labels: {
            show: true,
            value: {
              fontSize: '1.5rem',
              fontFamily: 'Public Sans',
              color: headingColor,
              offsetY: -15,
              formatter: function (val) {
                return parseInt(val) + '%';
              }
            },
            name: {
              offsetY: 20,
              fontFamily: 'Public Sans'
            }
          }
        },
      },
    },
    legend: {
      show: true,
      position: 'bottom',
      offsetY: 5,
    },
  };

  const statisticsChart = new ApexCharts(candidateStatistics, candidateChartConfig);
  statisticsChart.render();
  // Update series data when select box changes
  $('#candidateStatistics').on('change', function () {
    var selectedIndex = this.selectedIndex;
    var selectBoxValue = $('#candidateStatistics').val();
    if ($('#candidateStatistics').val() == 'job' && selectedIndex === 0) {
      var data = statisticsList;
      allCountCandidate(data);
    }
    else {
      var URL;
      if(selectedIndex === 0){
        URL='/candidateCountUnderTeam';
      }
      else{URL='/candidateByInterviewResult'};
      fetch(URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [header]: token,
          'X-XSRF-Token': token
        },
        body: JSON.stringify(selectBoxValue)
      })
        .then(response => {
          if (!response.ok) {
            console.log("Fail");
            throw new Error("Response is not ok");
          }

          return response.json();
        })
        .then(data => {
          var resultCount = [data[0], data[1], data[2], data[3], data[4],data[5],data[6]];
          const totalUpdated = resultCount.reduce((sum, value) => sum + value, 0);
          const percentagesUpdated = resultCount.map(value => parseFloat((value / totalUpdated * 100).toFixed(2)));
          var totalCandidate = data[0] + data[1] + data[2] + data[3] + data[4]+data[5]+data[6];
          allCandidate.innerHTML = totalCandidate; failedCount.innerHTML = data[1]
          passedCount.innerHTML = data[0]; pendingCount.innerHTML = data[2];
          cancelCount.innerHTML = data[3]; nextCount.innerHTML = data[4];
          jobAccepCount.innerHTML=data[5];notReachCount.innerHTML=data[6];
          statisticsChart.updateOptions({
            series: percentagesUpdated,
          });
        })
        .catch(error =>
          console.error('Error :' + error)
        );

    }

  });

  //select box for team 
  $('#teamStatistics').on('change', function () {
    var index = this.selectedIndex;
    const team = teamList[index - 1];

    // Get a reference to the select element
    const selectElement = document.getElementById("candidateStatistics");

    if ($('#teamStatistics').val() == 'All') {
      selectElement.innerHTML = "";
      const allOption = document.createElement("option");
      allOption.textContent = "All";
      allOption.value = 'job';
      allOption.style.textAlign = "center";
      selectElement.appendChild(allOption);

      //to set candidate for all
      var data = statisticsList;
      allCountCandidate(data);
      //set all jobDetail list in options
      jobDetailList.forEach(jobDetail => {
        const option = document.createElement("option");
        option.textContent = `${jobDetail.jobTitle.name} ${jobDetail.jobLevel !=null ? '('+ jobDetail.jobLevel.name +')' : ''}`;
        option.value = jobDetail.id;
        option.style.textAlign = "center";
        selectElement.appendChild(option);
      });
    }
    else {
      // Clear existing options
      selectElement.innerHTML = "";

      // Create a new 'All' option
      const allOption = document.createElement("option");
      allOption.textContent = "All";
      allOption.value = team.id;
      allOption.style.textAlign = "center";
      selectElement.appendChild(allOption);

      //to get candidat count under team
      fetch('/candidateCountUnderTeam', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [header]: token,
          'X-XSRF-Token': token
        },
        body: JSON.stringify(team.id)
      })
        .then(response => {
          if (!response.ok) {
            console.log("Fail");
            throw new Error("Response is not ok");
          }

          return response.json();
        })
        .then(data => {
          var resultCount = [data[0], data[1], data[2], data[3], data[4],data[5],data[6]];
          const totalUpdated = resultCount.reduce((sum, value) => sum + value, 0);
          const percentagesUpdated = resultCount.map(value => parseFloat((value / totalUpdated * 100).toFixed(2)));
          var totalCandidate = data[0] + data[1] + data[2] + data[3] + data[4]+data[5]+data[6];
          console.log('total candidate under team : '+totalCandidate)
          allCandidate.innerHTML = totalCandidate; failedCount.innerHTML = data[1]
          passedCount.innerHTML = data[0]; pendingCount.innerHTML = data[2];
          cancelCount.innerHTML = data[3]; nextCount.innerHTML = data[4];
          jobAccepCount.innerHTML=data[5];notReachCount.innerHTML=data[6];
          statisticsChart.updateOptions({
            series: percentagesUpdated,
          });
        })
        .catch(error =>
          console.error('Error :' + error)
        );

      team.jobDetails.forEach(jobDetail => {
        const option = document.createElement("option");
        option.textContent = `${jobDetail.jobTitle.name} ${jobDetail.jobLevel !=null ? '('+ jobDetail.jobLevel.name +')' : ''}`;
        option.value = jobDetail.id;
        option.style.textAlign = "center";
        selectElement.appendChild(option);
      });
    }
  })

  //get candidateCount
function allCountCandidate(data) {
  const totalUpdated = data.reduce((sum, value) => sum + value, 0);
  const percentagesUpdated = data.map(value => parseFloat((value / totalUpdated * 100).toFixed(2)));
  var totalCandidate = passedList + failedList + pendingList + cancelList + nextInterviewList+jobOfferedList+notReachList;
  allCandidate.innerHTML = totalCandidate; failedCount.innerHTML = failedList
  passedCount.innerHTML = passedList; pendingCount.innerHTML = pendingList;
  cancelCount.innerHTML = cancelList; nextCount.innerHTML = nextInterviewList;
  jobAccepCount.innerHTML=jobOfferedList;notReachCount.innerHTML==notReachList;
  statisticsChart.updateOptions({
    series: percentagesUpdated,
  });
}

}


const jobOfferStatisticsChart = document.querySelector('#jobOfferStatisticsChart');
if (typeof jobOfferStatisticsChart !== 'undefined' && jobOfferStatisticsChart !== null) {
  const originalData = [
    {
      name: 'Post',
      data: [totalPost,0,0], 
    },
    {
      name: 'Job Accepted',
      data: [0,jobAcceptedList,0],
    },
    {
      name: 'Passed(Offered)',
      data: [0,0,jobOfferedList],
    },
    {
      name: 'Passed(Not offered)',
      data: [0,0,passedList],
    },
  ];

  const series = originalData.map(entry => {
    return {
      name: entry.name,
      data: entry.data, // Use the initial data
    };
  });

  const jobOfferChartConfig = {
    chart: {
      type: 'bar',
      stacked: true,
    },
    xaxis: {
      categories: ['Post','Job Accepted','Passed'],
    },
    series: series,
    colors: [
      config.colors.primary,
      config.colors.success,
      config.colors.secondary,
      '#0077B5',
    ],
    plotOptions: {
      bar: {
        horizontal: false,
      },
    },
    legend: {
      show: true,
      position: 'bottom',
      offsetY: 5,
    },
  };

  const jobOfferChart = new ApexCharts(jobOfferStatisticsChart, jobOfferChartConfig);
  jobOfferChart.render();

    // Update series data when select box changes
    $('#jobOfferJobDetailStatistics').on('change', function () {
      var selectedIndex = this.selectedIndex;
      var selectBoxValue = $('#jobOfferJobDetailStatistics').val();
      if ($('#jobOfferJobDetailStatistics').val() == 'job' && selectedIndex === 0) {
          updateDataForJobOfferStatistics(totalPost,jobAcceptedList,jobOfferedList,passedList);
      }
      else {
        var URL;
        if(selectedIndex === 0){
          URL='/jobOfferStatisticsUnderTeam';
        }
        else{URL='/jobOfferStatisticsUnderJobDetail'};
        fetch(URL, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            [header]: token,
            'X-XSRF-Token': token
          },
          body: JSON.stringify(selectBoxValue)
        })
          .then(response => {
            if (!response.ok) {
              console.log("Fail");
              throw new Error("Response is not ok");
            }
  
            return response.json();
          })
          .then(data => {
              updateDataForJobOfferStatistics(data[0],data[1],data[2],data[3])
          })
          .catch(error =>
            console.error('Error :' + error)
          );
  
      }
  
    });

    //select box for team 
    $('#jobOfferTeamStatistics').on('change', function () {
      var index = this.selectedIndex;
      const team = teamList[index - 1];
  
      // Get a reference to the select element
      const selectElement = document.getElementById("jobOfferJobDetailStatistics");
  
      if ($('#jobOfferTeamStatistics').val() == 'All') {
        selectElement.innerHTML = "";
        const allOption = document.createElement("option");
        allOption.textContent = "All";
        allOption.value = 'job';
        allOption.style.textAlign = "center";
        selectElement.appendChild(allOption);
  
        //to update jobOfferStatistics for all
        updateDataForJobOfferStatistics(totalPost,jobAcceptedList,jobOfferedList,passedList);

        //set all jobDetail list in options
        jobDetailList.forEach(jobDetail => {
          const option = document.createElement("option");
          option.textContent = `${jobDetail.jobTitle.name} ${jobDetail.jobLevel !=null ? '('+ jobDetail.jobLevel.name +')' : ''}`;
          option.value = jobDetail.id;
          option.style.textAlign = "center";
          selectElement.appendChild(option);
        });
      }
      else {
        // Clear existing options
        selectElement.innerHTML = "";
  
        // Create a new 'All' option
        const allOption = document.createElement("option");
        allOption.textContent = "All";
        allOption.value = team.id;
        allOption.style.textAlign = "center";
        selectElement.appendChild(allOption);
        //to get candidat count under team
        fetch('/jobOfferStatisticsUnderTeam', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            [header]: token,
            'X-XSRF-Token': token
          },
          body: JSON.stringify(team.id)
        })
          .then(response => {
            if (!response.ok) {
              console.log("Fail");
              throw new Error("Response is not ok");
            }
  
            return response.json();
          })
          .then(data => {
              updateDataForJobOfferStatistics(data[0],data[1],data[2],data[3])
          })
          .catch(error =>
            console.error('Error :' + error)
          );
  
        team.jobDetails.forEach(jobDetail => {
          const option = document.createElement("option");
          option.textContent = `${jobDetail.jobTitle.name} ${jobDetail.jobLevel !=null ? '('+ jobDetail.jobLevel.name +')' : ''}`;
          option.value = jobDetail.id;
          option.style.textAlign = "center";
          selectElement.appendChild(option);
        });
      }
    })

//method for add job offer statistics data
function updateDataForJobOfferStatistics(post,jobAccept,jobOffer,passed){
      var updatedData = [ {
          name: 'Post',
          data: [post,0,0], 
        },
        {
          name: 'Job Accepted',
          data: [0,jobAccept,0],
        },
        {
          name: 'Passed(Offered)',
          data: [0,0,jobOffer],
        },
        {
          name: 'Passed(Not offered)',
          data: [0,0,passed],
        },]
  
      jobOfferChart.updateSeries(updatedData);
}

}







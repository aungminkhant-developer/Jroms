/**
 * Dashboard Analytics
 */

'use strict';

var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
(function () {
  let cardColor, headingColor, axisColor, shadeColor, borderColor;

  cardColor = config.colors.white;
  headingColor = config.colors.headingColor;
  axisColor = config.colors.axisColor;
  borderColor = config.colors.borderColor;

//Total Candidate Chart
var totalCandidate=candidateCountList[0]+candidateCountList[1]+candidateCountList[2]+candidateCountList[3]+
                   candidateCountList[4]+candidateCountList[5]+candidateCountList[6]+candidateCountList[7]+
                   candidateCountList[8]+candidateCountList[9]+candidateCountList[10]+candidateCountList[11];
$(document).ready(function () {
  const totalCandidateChartOptions = {
    chart: {
      type: 'column',
      height: 400,
      stacked: true,
      toolbar: { show: false }
    },
    title: {
      text: 'Total Candidate ('+totalCandidate+')'
    },
    xAxis: {
      categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },
    yAxis: {
      title: {
        text: 'Candidate'
      },
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },
    series: [   
      {
        name: 'Total',
        color: config.colors.primary,
        data: [candidateCountList[0],candidateCountList[1],candidateCountList[2],candidateCountList[3],
               candidateCountList[4],candidateCountList[5],candidateCountList[6],candidateCountList[7],
               candidateCountList[8],candidateCountList[9],candidateCountList[10],candidateCountList[11]]
      },
      {
        name: 'Job Accepted',
        color:  config.colors.success,
        data: [candidateCountList[12],candidateCountList[13],candidateCountList[14],candidateCountList[15],
               candidateCountList[16],candidateCountList[17],candidateCountList[18],candidateCountList[19],
               candidateCountList[20],candidateCountList[21],candidateCountList[22],candidateCountList[23]]
      },
      {
        name: 'Failed',
        color: '#f0510d',
        data: [candidateCountList[24],candidateCountList[25],candidateCountList[26],candidateCountList[27],
               candidateCountList[28],candidateCountList[29],candidateCountList[30],candidateCountList[31],
               candidateCountList[32],candidateCountList[33],candidateCountList[34],candidateCountList[35]]
      },
    ],
    plotOptions: {
      column: {
        borderRadiusTopLeft: 10,
        borderRadiusTopRight: 10
      }
    },
    legend: {
      enabled: true,
      itemStyle: {
        fontSize: '12px'
      }
    },
    credits: {
      enabled: false
    }
  };

  // Initialize the chart
  const totalCandidateChart = Highcharts.chart('totalCandidateChartDisplay', totalCandidateChartOptions);

  // Event handler for the select box change
  $('#totalCandidateChart').on('change', function () {
    var data=$("#totalCandidateChart").val();
    fetch('/getCandidateCountByMonth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [header]: token,
        'X-XSRF-Token': token
      },
      body: JSON.stringify(data)
    })
      .then(response => {
        if (!response.ok) {
          console.log("Fail");
          throw new Error("Response is not ok");
        }

        return response.json();
      })
      .then(data => {
        var list=[data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11]]
        var candidatePassed=[data[12],data[13],data[14],data[15],data[16],data[17],data[18],data[19],data[20],data[21],data[22],data[23]]
        var candidateFailed=[data[24],data[25],data[26],data[27],data[28],data[29],data[30],data[31],data[32],data[33],data[34],data[35]]
        var total=data[0]+data[1]+data[2]+data[3]+data[4]+data[5]+data[6]+data[7]+data[8]+data[9]+data[10]+data[11];
        totalCandidateChart.series[0].setData(list);
        totalCandidateChart.series[1].setData(candidatePassed);
        totalCandidateChart.series[2].setData(candidateFailed);;
        totalCandidateChart.title.update({ text: 'Total Candidate ('+total+')'});
      })
      .catch(error =>
        console.error('Error :' + error)
      );    
  });
});

  //total job Offer chart
  var totalJobOfferCount=jobOfferCountList[0]+jobOfferCountList[1]+jobOfferCountList[2]+jobOfferCountList[3]+
                          jobOfferCountList[4]+jobOfferCountList[5]+jobOfferCountList[6]+jobOfferCountList[7]+
                          jobOfferCountList[8]+jobOfferCountList[9]+jobOfferCountList[10]+jobOfferCountList[11];
  const totalJobOfferChartOptions = {
    chart: {
      type: 'line',
      height: 400,
      stacked: true,
      toolbar: { show: false }
    },
    title: {
      text: 'Total Job Offer ('+totalJobOfferCount+')'
    },
    xAxis: {
      categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },
    yAxis: {
      title: {
        text: 'Job Offer'
      },
      labels: {
        style: {
          fontSize: '12px'
        }
      }
    },
    series: [   
      {
        name: 'Total',
        color: config.colors.primary,
        data: [jobOfferCountList[0],jobOfferCountList[1],jobOfferCountList[2],jobOfferCountList[3],
                jobOfferCountList[4],jobOfferCountList[5],jobOfferCountList[6],jobOfferCountList[7],
                jobOfferCountList[8],jobOfferCountList[9],jobOfferCountList[10],jobOfferCountList[11]]
      },
    ],
    plotOptions: {
      column: {
        borderRadiusTopLeft: 10,
        borderRadiusTopRight: 10
      }
    },
    legend: {
      enabled: true,
      itemStyle: {
        fontSize: '12px'
      }
    },
    credits: {
      enabled: false
    }
  };

  // Initialize the chart
  const totalJobOfferChart = Highcharts.chart('totalJobOfferChart', totalJobOfferChartOptions);

  // Event handler for the select box change
  $('#jobOfferChartYearSelectBox').on('change', function () {
    var data=$("#jobOfferChartYearSelectBox").val();
    fetch('/getJobOfferCountByMonth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        [header]: token,
        'X-XSRF-Token': token
      },
      body: JSON.stringify(data)
    })
      .then(response => {
        if (!response.ok) {
          console.log("Fail");
          throw new Error("Response is not ok");
        }

        return response.json();
      })
      .then(data => {
        var list=[data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11]]
        var total=data[0]+data[1]+data[2]+data[3]+data[4]+data[5]+data[6]+data[7]+data[8]+data[9]+data[10]+data[11];
        totalJobOfferChart.series[0].setData(list);
        totalJobOfferChart.title.update({ text: 'Total Job Offer ('+total+')'});
      })
      .catch(error =>
        console.error('Error :' + error)
      );    
  });
})();

var searching = false;

function simpleSlider() {
  var ageSlider = $("#your-age");
  var rangeValue = function() {
    //update value
    $('.value').html(ageSlider.val());
   }

  ageSlider.on("input", rangeValue);
}

function doubleSlider() {
    // Initiate Slider
      $('#slider-range').slider({
        range: true,
        min: 0,
        max: 60,
        step: 1,
        values: [16, 28]
      });

      // Move the range wrapper into the generated divs
      $('.ui-slider-range').append($('.range-wrapper'));

      // Apply initial values to the range container
      $('.range').html('<span class="range-value">' + $('#slider-range').slider("values", 0).toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + '</span><span class="range-divider"></span><span class="range-value">' + $("#slider-range").slider("values", 1).toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + '</span>');

      // Show the gears on press of the handles
      $('.ui-slider-handle, .ui-slider-range').on('mousedown', function() {
        $('.gear-large').addClass('active');
      });

      // Hide the gears when the mouse is released
      // Done on document just incase the user hovers off of the handle
      $(document).on('mouseup', function() {
        if ($('.gear-large').hasClass('active')) {
          $('.gear-large').removeClass('active');
        }
      });

      // Rotate the gears
      var gearOneAngle = 0,
        gearTwoAngle = 0,
        rangeWidth = $('.ui-slider-range').css('width');

      $('.gear-one').css('transform', 'rotate(' + gearOneAngle + 'deg)');
      $('.gear-two').css('transform', 'rotate(' + gearTwoAngle + 'deg)');

      $('#slider-range').slider({
        slide: function(event, ui) {

          // Update the range container values upon sliding

          $('.range').html('<span class="range-value">' + ui.values[0].toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + '</span><span class="range-divider"></span><span class="range-value">' + ui.values[1].toString().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1,") + '</span>');

          // Get old value
          var previousVal = parseInt($(this).data('value'));

          // Save new value
          $(this).data({
            'value': parseInt(ui.value)
          });

          // Figure out which handle is being used
          if (ui.values[0] == ui.value) {

            // Left handle
            if (previousVal > parseInt(ui.value)) {
              // value decreased
              gearOneAngle -= 7;
              $('.gear-one').css('transform', 'rotate(' + gearOneAngle + 'deg)');
            } else {
              // value increased
              gearOneAngle += 7;
              $('.gear-one').css('transform', 'rotate(' + gearOneAngle + 'deg)');
            }

          } else {

            // Right handle
            if (previousVal > parseInt(ui.value)) {
              // value decreased
              gearOneAngle -= 7;
              $('.gear-two').css('transform', 'rotate(' + gearOneAngle + 'deg)');
            } else {
              // value increased
              gearOneAngle += 7;
              $('.gear-two').css('transform', 'rotate(' + gearOneAngle + 'deg)');
            }

          }

          if (ui.values[1] === 60) {
            if (!$('.range-alert').hasClass('active')) {
              $('.range-alert').addClass('active');
            }
          } else {
            if ($('.range-alert').hasClass('active')) {
              $('.range-alert').removeClass('active');
            }
          }
        }
      });

      // Prevent the range container from moving the slider
      $('.range, .range-alert').on('mousedown', function(event) {
        event.stopPropagation();
      });
}

function post(path, params, method='post') {

  // The rest of this code assumes you are not using a library.
  // It can be made less verbose if you use one.
  var $form = $('<form>', {
    method: method,
    action: path
  });

  for (const key in params) {
    if (params.hasOwnProperty(key)) {
    $('<input>', {
        type: 'hidden',
        name: key,
        value: params[key]
      }).appendTo($form);
    }
  }

  $('body').append($form);
  $form.submit();
}

function submit() {
  $("form").on("submit", (e) => {
     var form = $("#form");
     $('<input>', {
        type: 'hidden',
        name: 'interlocutor-age-min',
        value: $("#slider-range").slider("values", 0)
     }).appendTo(form);

     $('<input>', {
      type: 'hidden',
      name: 'interlocutor-age-max',
      value: $("#slider-range").slider("values", 1)
     }).appendTo(form);
    onSearch(true);

    e.stopPropagation();
  });
  }

function onSearch(isSearching) {
    searching = isSearching;
    if (isSearching) {
        console.log('start searching');
        checkNumberOfUsers();
        $("#form-container").hide();
        $("#loader").show();
    } else {
        console.log('stop searching');
        $("#loader").hide();
        $("#form-container").show();
    }
}

function checkNumberOfUsers() {
    $.ajax({
      url: '/usersnumber',
      success: (data) => {
        //first request will give not correct result, user's request don't counted yet
        $("#users_number_info").html(data < 2 ? 'no users are currently chatting' :
            data + ' users are currently chatting');
      },
      complete: () => {
        if (searching)
            setInterval(checkNumberOfUsers, 5000);
      }

    });
}


$(function() {
  simpleSlider();
  doubleSlider();
  submit();

  $("#cancel").on('click', () => {
        onSearch(false);
        console.log("CANCELED");
        var deleteRequest = new XMLHttpRequest();
        deleteRequest.open("DELETE", "/cancel");
        deleteRequest.setRequestHeader('x-csrf-token', $('input[name="_csrf"]').val());
        deleteRequest.send();
    });
});

var usernameTaken = false;
var emailTaken = false;

$(function () {
    $("#username").on('input', (e) => {
        $.ajax({
            url: '/checkusername',
            data: 'username=' + $('#username').val(),
            success: (data) => {
              if (data) {
                $('#username_taken').show();
                usernameTaken = true;
              }
              else {
                $('#username_taken').hide();
                usernameTaken = false;
              }
                $('#submit-btn').prop('disabled', usernameTaken || emailTaken);
            }
        });
    });

    $("#email").on('input', (e) => {
        $.ajax({
            url: '/checkemail',
            data: 'email=' + $('#email').val(),
            success: (data) => {
              if (data) {
                $('#email_taken').show();
                emailTaken = true;
              }
              else {
                $('#email_taken').hide();
                emailTaken = false;
              }
              $('#submit-btn').prop('disabled', usernameTaken || emailTaken);
            }
        });
    });
});
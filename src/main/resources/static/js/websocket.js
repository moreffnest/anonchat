var stompClient = null;
var isConnected = false;
var $conversationId = -1;
var $sessionId = -1;
$intersectedListsTypes = '';

function connect() {
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect, onError);
}

function onConnect(frame) {
    setConnected(true);
    $conversationId = $("#conversation_id").html();
    $sessionId = $('#session_id').html();
    $intersectedListsTypes = JSON.parse($('#intersected_lists_types').html());

    if ($conversationId) {
        console.log('connected: ' + frame);
        stompClient.subscribe('/room/' + $conversationId, (message) => {
                showMessage(JSON.parse(message.body));
            }
        );
    }
}

function setConnected(connected) {
    isConnected = connected;
    if (connected) {
        $("#find-next").hide();
    }
    else {
        $("#find-next").show();
    }
}

function onError(error) {
    console.error("Error occurred: " + error)
}

function showMessage(message) {
    switch (message.type) {
        case 'SIMPLE':
            //print only time and content
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                 + message.content + "</p>");
            if (message.senderSessionId != $sessionId)
                $p.css("background", "#64D7C4");

            $("#message-field").append($p);
            break;
        case 'DISCONNECT':
            //print only time and content
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                + 'Your interlocutor has been disconnected.' + "</p>");
            $p.css("background", "#E4CF47");
            $("#message-field").append($p);
            disconnect();
            break;
        case 'HELP':
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                + message.content + "</p>");
            $p.css("background", "#E4CF47");
            $("#message-field").append($p);
            break;
        default:
            console.log('there is no such type of message :(');
    }

    $('#message-field').scrollTop($("#message-field").prop('scrollHeight'));
}


function disconnect() {
    if (isConnected) {
        var now = new Date();
        if (stompClient != null) {
            $p = $("<p>" + now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds() + '<br>'
                            + 'You have been disconnected.' + "</p>");
            $p.css("background", "#E4CF47");
            $("#message-field").append($p);
            $('#message-field').scrollTop($("#message-field").prop('scrollHeight'));
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("disconnected");
    }
}

function send() {
    var $message = $("#message").val();
    if ($message) {
        stompClient.send("/app/room/" + $conversationId, {},
            JSON.stringify({'content': $message,
                            'type': 'SIMPLE',
                            'senderSessionId': $('#session_id').html()
            })
        );
        $("#message").val('');
    }
}

$(function () {
    connect();
    $("#disconnect").on('click', () => {
        //send only 1 time to get server informedmdcmd
        stompClient.send("/app/room/" + $conversationId, {},
            JSON.stringify({'type': 'DISCONNECT'})
        );
        disconnect();
    });
    $("#send-button").on('click', () => send());
    $("#message").on('keydown', (e) => {
        if (e.which === 13 && !e.shiftKey) {
            e.preventDefault();
            send();
        }
    });
    $('#switch').on('change', function ()  {
        var show = $(this).prop('checked');
        if (show) {
            //always turn on
            $('.anon-button').each((index, element) => {
                $(element).show();
            });
            //turn on only necessary buttons
            for (var type of $intersectedListsTypes) {
                $('button[data-type="' + type + '"]').show();
            }
        } else {
            //always turn off
            $('.anon-button').each((index, element) => {
                $(element).hide();
            });
            //turn off only necessary buttons
            for (var type of $intersectedListsTypes) {
                $('button[data-type="' + type + '"]').hide();
            }
        }
    });
    $(".help-button").on('click', function () {
        stompClient.send("/app/room/" + $conversationId, {},
            JSON.stringify({
                'type': 'HELP',
                'content': $(this).attr('data-type')
                })
        );
    });
});
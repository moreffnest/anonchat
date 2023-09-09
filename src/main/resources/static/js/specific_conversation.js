function parseMessages(username, messages) {
    for (var message of messages) {
        switch (message.type) {
        case 'SIMPLE':
            //print only time and content
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                 + message.content + "</p>");
            if (message.senderUsername != username)
                $p.css("background", "#64D7C4");

            $("#messages-field").append($p);
            break;
        case 'DISCONNECT':
            //print only time and content
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                + 'Your interlocutor has been disconnected.' + "</p>");
            $p.css("background", "#E4CF47");
            $("#messages-field").append($p);
            break;
        case 'HELP':
            $p = $("<p>" + message.time.split('@')[1] + '<br>'
                + message.content + "</p>");
            $p.css("background", "#E4CF47");
            $("#messages-field").append($p);
            break;
        default:
            console.log('there is no such type of message :(');
        }
    }
}



$(function() {
    $username = $('#username').html();
    $messages = JSON.parse($('#messages').html());
    parseMessages($username, $messages);
});
<html>
<head>
    <script type="text/javascript" src="./static/jquery.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            var video_websocket;
            if ('WebSocket' in window) {
                video_websocket = new WebSocket("ws://"+window.location.hostname+":8080/water_park/webSocketServer");
            } else if ('MozWebSocket' in window) {
                video_websocket = new MozWebSocket("ws://\""+window.location.hostname+":8080/water_park/webSocketServer");
            } else {
                video_websocket = new SockJS("http://localhost:8080/water_park/sockjs/webSocketServer");
            }

            video_websocket.onopen = function (evnt) {
                console.log("WebSocket连接成功");
            };
            video_websocket.onmessage = function (evnt) {
                var data = evnt.data;
                console.log("onmessage");
                var pic = "data:image;base64,"+data;
                $("#player").attr("src",pic);

            };
            video_websocket.onerror = function (evnt) {
                console.log("WebSocket连接出错");
                //$("#msgcount").append("WebSocket链接出错！<br/>");
            };
            video_websocket.onclose = function (evnt) {

                console.log("WebSocket连接关闭");
                //$("#msgcount").append("WebSocket链接关闭！<br/>");
            };
        });

    </script>
</head>
<body>
    <img width="100%" id="player" src=""/>
</body>
</html>

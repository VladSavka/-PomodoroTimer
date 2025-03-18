onmessage = function (e) {
    let num = 60;
    let count = setInterval(function () {
        postMessage(--num);
        if (num <= 0) {
            clearInterval(count);
            close();
        }
    }, 1000);
}
// the same as $(document).ready, but no JQuery as dependency
document.addEventListener("DOMContentLoaded", function() {
    window.pad = function(n, width, z) {
        z = z || '0';
        n = n + '';
        return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
    }    
});

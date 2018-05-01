document.addEventListener("DOMContentLoaded", function() {
    var div = document.getElementById('rectangular-selection-area'), x1 = 0, y1 = 0, x2 = 0, y2 = 0;
    function reCalc() {
        var x3 = Math.min(x1,x2);
        var x4 = Math.max(x1,x2);
        var y3 = Math.min(y1,y2);
        var y4 = Math.max(y1,y2);
        div.style.left = x3 + 'px';
        div.style.top = y3 + 'px';
        div.style.width = x4 - x3 + 'px';
        div.style.height = y4 - y3 + 'px';
    }
    function checkWhetherIsWithin(R1,R2){
      return (R2.x+R2.width) < (R1.x+R1.width) && (R2.x) > (R1.x) && (R2.y) > (R1.y) && (R2.y+R2.height) < (R1.y+R1.height)
    }
    function intersection(a, b) {
        return (a.left <= b.right &&
                b.left <= a.right &&
                a.top <= b.bottom &&
                b.top <= a.bottom)
    }
    
    function gettingNodesUnderSelection(){
        var items = document.getElementsByClassName("node-item")
        var bclr  = div.getBoundingClientRect()
        for (var i = 0;i<items.length;i++) {
            var val = items[i]
            var val_bclr = val.getBoundingClientRect()
            if(intersection(val_bclr,bclr)){
                miktau.events.call_select_only_select_node(val.getAttribute("data-fpath"))
            }
            // debugger;
        }
    }
    
    onmousedown = function(e) {
        div.hidden = 0;
        x1 = e.pageX;
        y1 = e.pageY;
        reCalc();
    };
    onmousemove = function(e) {
        x2 = e.pageX;
        y2 = e.pageY;
        reCalc();
        gettingNodesUnderSelection();
    };
    onmouseup = function(e) {
        // debugger;
        div.hidden = 1;
    };
    
});


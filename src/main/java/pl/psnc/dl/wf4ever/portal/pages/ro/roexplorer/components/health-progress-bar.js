$(function() {
    $( "#health-progress-bar").progressbar();
});

function setValue(v) {
    $( "#health-progress-bar").progressbar({
      value: v
    });
}

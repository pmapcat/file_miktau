// the same as $(document).ready, but no JQuery as dependency
document.addEventListener("DOMContentLoaded", function() {
  var iinput  = document.querySelector('input[name=tags-to-delete]')
    
  new Tagify(iinput, {
      duplicates: false,
      maxTags: 1000,
      enforceWhitelist: true,
      whitelist: ["these","tags","can","be","removed"]
  })
  new Tagify(document.querySelector('input[name=tags-to-add]'), {
      duplicates: false,
      maxTags: 1000,
      enforceWhitelist: false,
      whitelist: ["lorem","ipsum","dolor","sit","amlet"]
  })
});

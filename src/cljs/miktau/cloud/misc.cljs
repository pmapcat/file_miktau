(ns miktau.cloud.misc)

(def meta-order
  (zipmap
   (map
    keyword
    ["@empty:is-empty?"
     "@modified:in-future"
     "@modified:today"
     "@modified:this-week"
     "@modified:this-month"
     "@modified:this-year"
     "@modified:long-ago"
     "@file-size:less than 1mb" 
     "@file-size:1—10mb"
     "@file-size:10—50mb"
     "@file-size:50—100mb"
     "@file-size:100—500mb"
     "@file-size:500-1000mb"
     "@file-size:more than 1000mb"])
   (range)))

(defn meta-rank
  [item]
  (meta-order item))


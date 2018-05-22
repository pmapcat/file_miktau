(ns miktau.maybe)

(defn maybe-yes?
  [option-point]
  (not (nil? (:yes option-point))))

(defn maybe-parse-int
  [input]
  (if (re-matches #"-?[0-9]+" (str input))
    {:yes  (js/parseInt (str input)) :err nil}
    {:yes  nil :err "Input does not match schema"}))

(defn maybe-wrap-fn
  []
  
  )

(defn maybe-apply
  [option-point apply-fn]
  (if (maybe-yes? option-point)
    (apply-fn option-point)
    option-point))

(maybe-apply
 (maybe-parse-int)
 
 )

(if (maybe-yes? (maybe-parse-int "sad"))
  (println "Input is: " ))

(defn maybe
  []
  )



(ns miktau.tools
  (:require  [clojure.string :as cljs-string]
             [clojure.set :as clojure-set]
             [clojure.walk :as cljs-walk]))

(defn is-meta-tag?
  [tag]
  (cljs-string/starts-with? (str (name tag)) "@"))

(defn paginate [current last-item]
  (let [delta 2
        left (- current delta)
        right (+ current delta 1)]
    (->>
     (filter #(or (= % 1) (= % last-item) (and (>= % left) (< % right))) (range 1 (inc last-item)))
     (reduce
      (fn [prev next]
        (let [prev-last (last prev)
              collapse?  (and  (number? prev-last) (> (- next prev-last) 1))]
          (cond
            (empty? prev)
            [next]
            collapse?
            (-> prev
                (conj "...")
                (conj next))
            :else
            (conj prev next)))) [])
     (map #(if (= % "...")
             {:page nil :name "..."   :cur?  false}
             {:page %   :name (str %) :cur? (= % current)})))))

(def in-utils-variable "BLAB")

(defn mik-parse-int
  "TESTED"
  [input or]
  (if (re-matches #"-?[0-9]+" (str input))
    (js/parseInt (str input))
    or))


(defn integerize-keyword-keys
  "Recursively transforms all map keys from keywords to integers.
   If cannot, leaves the key as it was before, e.g. :keyword"
  [m]
  (let [f (fn [[k v]] (if (keyword? k) [(mik-parse-int (str (name k)) k) v] [k v]))]
    ;; only apply to maps
    (cljs-walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))




(defn clean-server-call-for-tests [server-call]
  (dissoc
   server-call
   :format
   :response-format))

(defn set-timeout
  [time-wait fun]
  (js/setTimeout (fn [_] (fun)) time-wait))

(defn pad
  "Zero Pad numbers - takes a number and the length to pad to as arguments"
   [n c pad-symbol] 
   (loop [s (str n)]  
     (if (< (count s) c) 
         (recur (str pad-symbol s)) 
         s)))

(defn pad-coll
  "Pad collection <coll> with <pad-with> until <n> is reached
   e.g. (pad-coll 10 [3 4] nil) => [3 4 nil nil nil nil nil nil nil nil]"
   [n coll pad-with] 
   (loop [s coll]  
     (if (< (count s) n) (recur (conj s pad-with))  s)))


(defn find-all-tags-in-string
  [string]
  (if (string? string)
    (into [] (filter #(<  (count (str %)) 20)  (re-seq #"[0-9А-Яа-яA-Za-z_]+" string)))
    []))

(defn allowed-tag-or?
  "TESTED"
  [input or-else]
  (if (and  (re-matches #"^[0-9А-Яа-яA-Za-z_]+$" (str input)) (<  (count (str input)) 20))
    input
    or-else))
(defn allowed-tag-or-include-empty?
  "TESTED"
  [input or-else]
  (if (empty? (str input)) input
      (allowed-tag-or? input or-else)))



;; (re-matches #"^[0-9А-Яа-яA-Za-z_]+$" (count "momavali_dro"))


(defn mik-parse-int-then-throw-error [input]
  (if (re-matches #"-?[0-9]+" (str input))
    (js/parseInt (str input))
    (throw  (js/Error. "Opps"))))

(defn seq-of-predicate?
  "TESTED"
  [items predicate]
  (cond
    (empty? items) false
    :else
    (=
     (count (filter predicate items))
     (count items))))

(defn parse-sorting-field
  [input]
  (let [input    (clojure.string/trim (str input))
        inverse? (cljs-string/starts-with? input "-")
        field    (if inverse? (apply str (rest input)) input)]
    (if (empty? input)
      {:inverse? false
       :field    :name}
      {:inverse? inverse?
       :field   (keyword field)})))

(defn jaccard
  [set-a set-b]
  (/
   (count (clojure-set/intersection set-a set-b))
   (count (clojure-set/union set-a set-b))))

(defn month-name
  [month-number]
  (get ["January" "February" "March"  "April"  "May"  "June" "July" "August" "September" "October" "November" "December"] (dec month-number)))

(defn scale-inplace
  [A B C D X]
  (+
   (* C (- 1  (/ (- X A) (- B A))))
   (* D (/ (- X A) (- B A)))))

(defn map-function-on-map-vals
  "Take a map and apply a function on its values. From [1].
   [1] http://stackoverflow.com/a/1677069/500207"
  [m f]
  (zipmap (keys m) (map f (vals m))))

(defn nested-group-by
  "Like group-by but instead of a single function, this is given a list or vec
   of functions to apply recursively via group-by. An optional `final` argument
   (defaults to identity) may be given to run on the vector result of the final
   group-by.
   !!careful, deep nesting is not supported
   usage example
   (def foo [[\"A\" 2011 \"Dan\"]
             [\"A\" 2011 \"Jon\"]
             [\"A\" 2010 \"Tim\"]
             [\"B\" 2009 \"Tom\"] ])
   (nested-group-by [first second] foo)"
  [fs coll & [final-fn]]
  (if (empty? fs)
    ((or final-fn identity) coll)
    (map-function-on-map-vals (group-by (first fs) coll)
                              #(nested-group-by (rest fs) % final-fn))))

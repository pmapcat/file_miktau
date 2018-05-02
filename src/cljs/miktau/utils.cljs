(ns miktau.utils
  (:require    [ajax.core :as ajax]
               [clojure.string :as cljs-string]
               [clojure.set :as clojure-set]
               [re-frame.core :as refe]))
(defn server-call [api-call on-success on-error]
  {:method :post
   :uri    (:url api-call)
   :response-format (ajax/json-response-format {:keywords? true})
   :format          (ajax/json-request-format)
   :timeout         8000
   :params  (:params api-call)
   :on-success [on-success]
   :on-failure [on-error]})
(defn clean-server-call-for-tests [server-call]
  (dissoc
   server-call
   :format
   :response-format))
(defn set-timeout
  [time-wait fun]
  (js/setTimeout (fn [_] (fun)) time-wait))

;; (set-timeout 3000 #(println "hello"))



(defn pad
  "Zero Pad numbers - takes a number and the length to pad to as arguments"
   [n c pad-symbol] 
   (loop [s (str n)]  
     (if (< (count s) c) 
         (recur (str pad-symbol s)) 
         s)))

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

(defn mik-parse-int
  "TESTED"
  [input or]
  (if (re-matches #"-?[0-9]+" (str input))
    (js/parseInt (str input))
    or))

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

(defn is-it-today?
  "TESTED"
  [db items]
  (every?
   identity
   (for [i items]
     (contains?  (i (:calendar-can-select db))
                 (keyword (str (i (db :date-now))))))))

(defn is-this-datepoint-selected?
  "TESTED"
  [db date-point]
  (every?
   identity
   (let [sel (:calendar-selected db)]
     (for [[key val] date-point]
       (=  (key sel) val)))))



(defn register-server-roundtrip
  [fx-name url-params request-params before-load success-fn error-fn]
  (let [success-name (keyword (str (name fx-name) "-success"))
        error-name    (keyword (str (name fx-name) "-error"))]
    (do
      (refe/reg-event-fx
       fx-name
       (fn [{:keys [db]} _]
         {:db (before-load db)
          :http-xhrio {:method          (url-params :method)
                       :uri             (url-params :url)
                       :params          (request-params db)
                       :response-format (ajax/json-response-format {:keywords? true})
                       :timeout         8000
                       :on-success      [success-name]
                       :on-failure      [error-name]}}))
      (refe/reg-event-fx
       success-name
       (fn [{:keys [db]} [_ bulk]]
         (success-fn db bulk)))
      (refe/reg-event-fx
       error-name
       (fn [{:keys [db]} [_ body]]
         (error-fn db body))
       (fn [{:keys [db]} [_ bulk]]
         (success-fn db bulk))))))


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


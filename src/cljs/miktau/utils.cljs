(ns miktau.utils
  (:require    [ajax.core :as ajax]
               [re-frame.core :as refe]))

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


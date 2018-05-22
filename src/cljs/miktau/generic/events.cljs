(ns miktau.generic.events
  (:require
   [re-frame.core :as refe]
   [miktau.meta-db :as meta-db]))

(refe/reg-event-fx
 :error
 (fn [{:keys [db]} [_ error]]
   {:db
    (-> db
        (meta-db/set-loading false)
        (meta-db/set-error   error)) 
    :log! (str  error)}))

(refe/reg-event-db
 :back
 (fn [db _]
   (.log js/console "registered <back> event")
   db))






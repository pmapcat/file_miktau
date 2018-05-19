(ns miktau.generic.events
  (:require
   [re-frame.core :as refe]))

(refe/reg-event-fx
 :http-error
 (fn [{:keys [db]} [_ response]]
   {:db (assoc db :loading? false)
    :log!  (str response)}))

(refe/reg-event-db
 :back
 (fn [db _]
   (.log js/console "registered <back> event")
   db))

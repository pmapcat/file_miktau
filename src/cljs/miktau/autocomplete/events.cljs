(ns miktau.autocomplete.events
  (:require [re-frame.core :as refe]))

(defn clear-cloud-click
  "TESTED"
  [{:keys [db]}  [_ redirector cloud-item]]
  {:db 
   (assoc db
       :filtering ""
       :cloud-selected #{cloud-item}
       :calendar-selected {})
   :fx-redirect redirector})

(refe/reg-event-fx :autocomplete/clear-cloud-click clear-cloud-click)

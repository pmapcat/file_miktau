(ns miktau.autocomplete.events
  (:require [re-frame.core :as refe]
            [miktau.tools :as utils]))

(defn clear-cloud-click
  "TESTED"
  [{:keys [db]}  [_ redirector cloud-item]]
  {:db
   (if (utils/is-meta-tag? cloud-item)
     (assoc db :meta-cloud-selected #{cloud-item})
     (assoc db :cloud-selected #{cloud-item}))
   :fx-redirect redirector})

(refe/reg-event-fx :autocomplete/clear-cloud-click clear-cloud-click)

(ns miktau.ui-log.events
  (:require [re-frame.core :as refe]))

(refe/reg-event-fx
 :ui-log/register-error
 (fn [{:keys [db]} [_ error]]
   {:db (assoc db :ui-log-error (str  error))
    :defer-fx-redirect [2000 :ui-log/drop-error]}))

(refe/reg-event-db
 :ui-log/drop-error
 (fn [db _]
   (assoc db :ui-log-error nil)))




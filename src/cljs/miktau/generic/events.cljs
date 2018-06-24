(ns miktau.generic.events
  (:require
   [re-frame.core :as refe]
   [miktau.meta-db :as meta-db]
   [day8.re-frame.undo :as undo-lib]))

(refe/reg-event-db
 :generic/init-static-page
 (fn [db [_ page]]
   (if (contains? #{:tos :oss-components :about :init} page)
     (meta-db/set-page-db db page)
     db)))

(refe/reg-event-fx
 :error
 (fn [{:keys [db]} [_ error]]
   {:db
    (-> db
        (meta-db/set-loading false)
        (meta-db/set-error   error)) 
    :log! (str  error)}))

;; undo config. (Will fetch data from server on every undo step)
(undo-lib/undo-config!
 {:reinstate-fn
  (fn [atom undo-state]
    (do (reset! atom undo-state)
        (refe/dispatch [:generic/update-page])))})


(refe/reg-event-fx
 :generic/update-page
 (fn [{:keys [db]} _]
   (condp = (meta-db/it-is-meta-page= db)
     :nodes      {:db db :fx-redirect [:nodes/get-app-data]}
     :cloud      {:db db :fx-redirect [:cloud/get-app-data]}
     :edit-nodes {:db db :fx-redirect [:edit-nodes/get-app-data]}
     {:db db :fx-redirect [:cloud/get-app-data]})))


(refe/reg-event-db
 :identity
 (fn [db _]
   db))


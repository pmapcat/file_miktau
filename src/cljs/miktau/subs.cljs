(ns miktau.subs
  (:require [re-frame.core :as refe]))

;; (reg-sub
;;  :total
;;  (fn [db _]
;;    (db :total)))

;; (reg-sub
;;  :total.pretty
;;  (fn [db _]
;;    (try
;;      (if (db :Total)
;;        (minutes->duration-pprint (db :total))
;;        "нет данных")
;;      (catch js/Error e "нет-данных"))))
;; (reg-sub
;;  :current.pretty
;;  (fn [db _]
;;    (let [minutes (get-in db [:cur-session :MinutesWorked])]
;;      (if minutes
;;        (minutes->duration-pprint minutes)
;;        "нет данных"))))


;; (reg-sub
;;  :auth?
;;  (fn [db _]
;;    (:auth? db)))


;; (reg-sub
;;  :user
;;  (fn [db _]
;;    (or  (:user db) {})))

;; (reg-sub
;;  :cur-session
;;  (fn [db _]
;;    (or (:cur-session db) {})))

;; (reg-sub
;;  :current-session
;;  (fn [db _]
;;    (:cur-session db)))

;; (reg-sub
;;  :users.sorted
;;  (fn [db _]
;;    (:group db)
;;    (sort-by  #(str (% :Status)
;;                    (% :User)) (:group db))))
;; (reg-sub
;;  :sessions.clustered
;;  (fn [db _]
;;    (reverse
;;     (sort-by
;;      first
;;      (group-by
;;       #(:date (time->nice-repr (:Started %)))
;;       (:sessions db))))))

;; (reg-sub
;;  :error
;;  (fn [db _]
;;    (db :has-error)))

;; (reg-sub
;;  :modal
;;  (fn [db _]
;;    (:modal db)))

;; (reg-sub
;;  :ins-and-outs
;;  (fn [db _]
;;    (or  (:ins-and-outs db) [])))

;; (reg-sub
;;  :loading?
;;  (fn [db _]
;;    (= :loading (db :status))))



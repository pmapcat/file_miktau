(ns miktau.effects
  (:require
   [day8.re-frame.http-fx]
   [re-frame.core :as refe]))

(refe/reg-fx
 :log!
 (fn [data]
   (.log js/console (str data))))

(refe/reg-fx
 :fx-redirect
 (fn [data]
   (refe/dispatch data)))


;; (reg-fx
;;  :store!
;;  (fn [data]
;;    (.setItem (aget js/window "localStorage")
;;              "tabel"
;;              (data->str data))))

;; (reg-fx
;;  :fx-redirect
;;  (fn [data]
;;    (dispatch data)))

;; (reg-fx
;;  :fx-load-data
;;  (fn [data]
;;    (try
;;      (let [datum (str->data (.getItem (aget js/window "localStorage") "tabel"))]
;;        (if-not (nil? datum)
;;          (dispatch [(data :on-success) datum])
;;          (dispatch [(data :on-error)])))
;;      (catch js/Error e (dispatch [(data :on-error)])))))

;; (reg-fx
;;  :notify!
;;  (fn [msg]
;;    (if-not (empty?  msg)
;;      (do
;;        (.log js/console (str msg))
;;        (c :notify js/window msg)))))

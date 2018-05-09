(ns miktau.app
  (:require
   [miktau.events]
   [miktau.effects]
   [miktau.subs]
   [miktau.query-building]
   [re-frame.core :as refe]
   [reagent.core :as reagent]
   [miktau.views.core :as views_core]))

;; (js/goog.DEBUG )

;; (defn init []
;;   ;; (aset  js/goog "DEBUG" true)
;;   (refe/dispatch-sync [:init])
;;   (reagent/render-component [views_core/main]
;;                             (.getElementById js/document "container"))
;;   )

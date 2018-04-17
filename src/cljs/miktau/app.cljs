(ns miktau.app
  (:require
   [miktau.events]
   [miktau.effects]
   [miktau.subs]
   [re-frame.core :as refe]
   [reagent.core :as reagent]
   [miktau.views.core :as views_core]))

(defn init []
  (refe/dispatch [:get-app-data])
  (reagent/render-component [views_core/main]
                            (.getElementById js/document "container"))
  )

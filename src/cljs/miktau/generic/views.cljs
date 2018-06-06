(ns miktau.generic.views
  (:require [miktau.generic.views-utils :as views-utils]
            [reagent.core :as reagent]))

(defn dropzone []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :margin-top "1em" :margin-bottom "1em"
            :border "dashed 1px gray"}}
   [:a.pure-button 
    [views-utils/icon "file_upload"]
    "Drop files here"]])

(defn choose-root
  []
  [:div
   {:style {:padding "5em", :border "dashed 1px gray"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "Choose root directory"]
    [:div.mik-flush-center.background-1
     {:style
      {:padding "5em", :margin-top "1em", :margin-bottom "1em", :border "dashed 1px gray"}}
     [:a
      [views-utils/icon "file_upload"]
      "Or Drop it here"]]]])

(defn processing
  []
  [:div.mik-flush-center
   {:style {:padding "5em" }}
   [:div
    [:h1 {:style {:font-size "4em"}}
     "Processing..."]
    [:div.mik-flush-center
     [:img {:src "/loading.gif"}]]]])


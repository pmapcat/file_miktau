(ns miktau.generic.views
  (:require [miktau.generic.views-utils :as views-utils]
            [miktau.tools :as utils]))

(defn dropzone []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :margin-top "1em" :margin-bottom "1em"
            :border "dashed 1px gray"}}
   [:a.pure-button 
    [views-utils/icon "file_upload"]
    "Drop files here"]])


(defn drop-conflict
  []
  [:div
   {:style {:padding "5em", :border "dashed 1px gray"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "Drop conflict"]
    [:p "It seems, that some files are not inside this: "
     [:b "../hello-world/ "] " directory"]
    [:b "Thus, I can either"]
    [:div.pure-u-1
     [:div.pure-u-1-3.pure-button "Copy them to current root directory"]
     [:div.pure-u-1-3.pure-button "Move them to current root directory"]
     [:div.pure-u-1-3.pure-button "Symlink them to current root directory"]
     [:div.pure-u-1-3.pure-button "Do nothing"]]]])


(defn initial
  []
  [:div
   {:style {:padding "5em"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "Hello"]
    [:p "Let's start by choosing a directory on which you would like to work on:"]
    [:input#clicko {:type "file" :directory "/" :style {:display "none"} :on-change  (fn [data] (println "[info]" (str (map #(aget % "path") (array-seq (aget data "target" "files"))))))}]
    [:div.pure-button.pure-button-primary {:on-click #(utils/js-call (utils/js-call js/document "getElementById" "clicko") "click")} [views-utils/icon "file_upload"] "Choose"]]])

(defn processing
  []
  [:div.mik-flush-center
   {:style {:padding "5em" }}
   [:div
    [:h1 {:style {:font-size "4em"}}
     "Doing work"]
    [:div.mik-flush-center
     [:img {:src "/loading.gif"}]]]])


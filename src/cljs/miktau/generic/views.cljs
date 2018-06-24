(ns miktau.generic.views
  (:require
   [re-frame.core :as refe]
   [miktau.generic.views-utils :as views-utils]))

(defn initial
  []
  [:div
   {:style {:padding "5em"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "Hello"]
    [:p "Let's start by choosing a directory on which you would like to work on:"]
    [:div.pure-button.pure-button-primary
     {:on-click #(refe/dispatch [:file-api/choose-root])}
     [views-utils/icon "file_upload"]
     "Choose"]]])

(defn processing
  []
  [:div.mik-flush-center
   {:style {:padding "5em" }}
   [:div
    [:h1 {:style {:font-size "4em"}}
     "Doing work"]
    [:div.mik-flush-center
     [:img {:src "/loading.gif"}]]]])


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

(defn tos
  []
  [:div
   {:style {:padding "5em"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "This is a terms of service page"]
    [:p "Let's start by declaring the fact, that this software does not imply any warranty. Like, at all"]
    [:p "And then add the "]
    
]])


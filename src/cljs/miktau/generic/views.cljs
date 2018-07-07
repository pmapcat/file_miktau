(ns miktau.generic.views
  (:require
   [re-frame.core :as refe]
   [miktau.generic.views-utils :as views-utils]
   [miktau.generic.tos :as tos-page]
   [miktau.generic.about :as about-page]))

(defn view-navigate
  []
  [:div.unstyled-link {:style {:padding-left "5px" }}
   "["
   [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:nodes/init-page #{}  #{} {}]) } "Nodes "]
   [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:cloud/init-page #{}  {}]) } "Cloud"]
   "]"])


(defn initial
  []
  [:div
   {:style {:padding "5em"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  { :style {:font-size "4em"}}
     "Hello :)"]
    [:p "It is " [:b.red "highly recommended "]  " to backup your working directory before you start messing with it"]
    [:p "Let's start by choosing a directory on which you would like to work on:"]
    [:div.pure-button.pure-button-primary
     {:on-click #(refe/dispatch [:file-api/swap-root])}
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
(defn static-page-template
  [body]
  [:div.pure-g {:style {:margin-bottom "200px" :padding "5em"}}
   [:div.background-1
    [:div.pure-u-1
     body]]
   [:div {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px" :background "white" :padding-right "20px" :box-shadow "grey 0px -1px 5px 0px"}}
    [:div.pure-u-1.padded-as-button
     [view-navigate]]]])

(defn tos
  []
  [static-page-template
   [tos-page/eula]])

(defn oss-components
  []
  [static-page-template
   [about-page/components]])

(defn about
  []
  [static-page-template
   [about-page/about]])



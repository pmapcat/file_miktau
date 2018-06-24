(ns miktau.generic.views
  (:require
   [re-frame.core :as refe]
   [miktau.generic.views-utils :as views-utils]
   [miktau.generic.tos :as tos]))

(defn view-navigate
  []
  [:div.mik-flush-right.unstyled-link {:style {:padding-right "5px" }}
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
(defn static-page-template
  [body]
  [:div.pure-g {:style {:margin-bottom "200px" :padding "5em"}}
   [:div.mik-flush-center.background-1
    body]
   [:div {:style {:position "fixed" :left "0px" :right "0px" :bottom "0px" :background "white" :padding-right "20px" :box-shadow "grey 0px -1px 5px 0px"}}
    [:div.pure-u-1.padded-as-button
     [view-navigate]]]])
(defn tos
  []
  [static-page-template
   [tos/eula {}]])

(defn about
  []
  [static-page-template
   [:div
    [:h1 "About"]
    [:h2 "The reasons behind this system"]
    [:p "In creating this system, we had the following goals in mind"]
    [:h2 "Tools "]
    [:p "We hoped to achieve these goals with the help of the following principles and tools"]
    [:ul
     [:li [:b "Spatial reasoning"] " The idea behind cloud is to use spatial reasoning to help in navigation"]
     [:li [:b "Mechanical memory reasoning"] " The idea behind cloud is to use spatial reasoning to help in navigation"]
     [:li [:b "Bulk renaming "] " The idea is to use bulk renaming  "]
     ]
    
    [:ul
     [:li "We hoped to "]
     ]

    
    
    [:]
    [:ul 
     [:li "Help to classify files in the system"]
     [:li "Help to work with existing file classifications: "
      [:ul
       [:li "ReclasClassify files in bulk"]
       ]
      ]
     [:li "Help to find files according"]
     ]
    
    
    [:p "TODO: write "]
    ]
   
   ]
  
  
  
  )



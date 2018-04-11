(ns miktau.views.core
  (:require [miktau.views.utils :as views-utils]
            [miktau.utils :as utils]
            [miktau.lorem :as lorem]))

(defn datetime-widget
  []
  [:div.pure-g
   [:div.pure-u-3-8
    [:select
     (for [i (range 1990 2019)]
       [:option i])]]
   [:div.pure-u-4-8
    [:select
     (for [i (range ["January" "February" "March"  "April"  "May"  "June" "July" "August" "September" "October" "November" "December"])]
       [:option i])]]
   [:div.pure-u-1-8
    [:select
     (for [i (range 1 32)]
       [:option i])]]])

(defn menu
  [first-item items]
  [:div.dropdown.padded-as-button
   first-item
   [:div.dropdown-content
    (for [item items]
      [:a.pure-button.mik-flush-left {:href "#" :style {:text-align "left"}} item])]])

(defn table-menu
  [first-item items]
  [:div.dropdown
   first-item
   [:div.dropdown-content {:style {:font-size "0.8em"}}
    (for [item items]
      [:a.pure-button {:href "#"} item])]])

(defn group-open []
  [:div
   (table-menu 
    [:span (views-utils/icon "folder_open") "Open them"]
    [[:span (views-utils/icon "folder_open") "In a single folder"]
     [:span (views-utils/icon "list") "Each individually"]
     [:span (views-utils/icon "filter") "Each in default program"]])])

(defn selection-cloud []
  [:div 
     (for [i  (lorem/random-word 10)]
       [:a.tag.padded-as-button 
        {:href "#"
         :class (rand-nth ["black"  "orange"])
         :style
         {:font-weight "300"
          :text-decoration "none"
          :font-size 
          (str (float (utils/scale-inplace 24 256 0.6 3.0 (rand-nth (range 24 256)))) "em")}}
        i " "])])

(defn general-cloud []
  [:div
   (for [i (lorem/random-word 10)]
     [:div
      [:div.mik-flush-right
       [:h2.padded-as-button.mik-cut-bottom.mik-cut-top.header-font.light-gray
        {:style {:padding-bottom "0px" :padding-top "0px"}}
        i]]
      (for [i  (lorem/random-word 10)]
        [:a.tag.padded-as-button
         {:href "#"
          :class (rand-nth ["gray" "black" "orange"])
          :style
          {:font-size
           (str (float (utils/scale-inplace 24 256 1 2 (rand-nth (range 24 256)))) "em")
           :font-weight "300"
           :text-decoration "none"}}
         i " "])])])

(defn  facet-group-select-time []
  [:div.pure-g
   [:div.pure-u-1.pure-g {:role "group"}
    [:h2.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font
     (views-utils/icon "timeline")
     "Filter on"]
    (for [item ["Today" "This week" "This month" "This year"]]
      [:a.pure-button {:href "#" :style {:text-align "left"}} 
       item])]
   
   [:div.pure-g {:role "group"}
    [:h2.pure-u-1.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font
     (views-utils/icon "line_style") "Year"]
    [:div.pure-u-1
     (for [item (reverse (range 2011 2019))]
       [:a.pure-button {:href "#" :style {:text-align "left"}}
        item])]]
   
   [:div.pure-u-1.pure-g {:role "group" }
    [:h2.pure-u-1.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font (views-utils/icon "date_range") "Month"]
    (for [item ["January" "February" "March"  "April"  "May"  "June" "July" "August" "September" "October" "November" "December"]]
      [:a.pure-button {:href "#" :style {:text-align "left"}} 
       item])]
   
   [:div.pure-g {:role "group"}
    [:h2.pure-u-1.mik-cut-bottom.mik-cut-top.padded-as-button.light-gray.header-font (views-utils/icon "date_range") "Day"]
    (for [item  (range 1 32)]
      [:a.pure-button.pure-u-1-5.tag {:href "#" :style {:text-align "left"}} 
       item])]])

(defn found-group
  []
  [:div
   [:div.mik-flush-right {:style {:text-align "right"}}
    [:a.pure-button {:href "#"} (views-utils/icon "file_upload") " Add files "]]
   [:h2.mik-cut-bottom.mik-cut-top.header-font
    {:style {:font-size "3em"}} "Found " [:b " 10 "] " files"]
   [:a.unstyled-link {:href "#"} "All"
    [:span {:style {:font-size "1em" :color "gray"}} "&raquo;"]]
   (for [i (butlast (lorem/random-word-random-size 10))]
     [:a.unstyled-link {:href "#"}
      i
      [:span {:style {:font-size "1em" :color "gray"}} "&raquo;"]])
   [:a.unstyled-link {:href "#"}
    (first (lorem/random-word))]])

(defn tagging-now-group []
  [:div.background-1.padded-as-button {:style {}}
   [:div.mik-flush-left
    (views-utils/icon "photo_size_select_small")
    "Selected " [:b "10"] " files"
    [:a.unstyled-link {:href "#"} " Unselect"]]
   (group-open)
   [:h2.header-font.light-gray.mik-cut-bottom.mik-flush-right
    (views-utils/icon "local_offer")
    "Remove tags from selection"]
   [:input {:name "tags-to-delete" :placeholder "Remove" :value "these,tags,can,be,removed"}]
   
   [:h2.header-font.light-gray.mik-cut-bottom.mik-flush-right
    (views-utils/icon "local_offer")
    "Add new tags" ]
   [:input {:name "tags-to-add" :placeholder "Add" :value "new,tag"}]
   [:div.mik-flush-right {:style {:margin-top "2em"}}
    [:a.pure-button {:href "#"}
     (views-utils/icon "save")
     "Save changes!"]]])

(defn  tag-tree []
  [:div.pure-g.mik-flush-right
   (for [i (lorem/random-word 5)]
     [:div.pure-u-1.pure-g {:role "group"}
      [:a.tag.black.mik-cut-bottom {:href "#" :style {:text-decoration "none"}}
       [:h2.mik-cut-bottom.mik-cut-top.padded-as-button.header-font.gray
        {:style {:padding-bottom "0px" :word-wrap "break-word"}}
        i "&middot;" i]]
      [:div.padded-as-button {:style {:padding-top "0px"}}
       (for [item (lorem/random-word (rand-int  20))]
         [:a.tag.black.body-font
          {:href "#"
           :style {:text-align "left"
                   :font-weight "600"
                   :text-decoration "none"}}
          item " " [:br]] )]])])

(defn radio-button
  [text selected?]
  (let [id (str (random-uuid))]
    [:label.pure-checkbox
     {:for id :style {:position "relative"}}
     [:input {:id id :value selected?, :style {} :type "checkbox" }]
     [:span {:style {:padding-bottom "5px"}}
      text]]))

(defn file-table [] 
  [:div.padded-as-button.background-1 
   [:div.pure-g {:style {:padding-bottom "1em"}}
    [:div.pure-u-2-24
     (views-utils/position-absolute
      {:top "4px"}
      (radio-button "" false))]
    [:div.pure-u-6-24 
     (table-menu "Name"
                 [[:span (views-utils/icon-rotated 180 "sort") " Order A-Z"]
                  [:span [:span (views-utils/icon "sort")] " Order Z-A"]])]
    [:div.pure-u-10-24]
    [:div.pure-u-6-24.mik-flush-right
     (table-menu "Modified"
                 ["Recent" "Older"])]]
   [:div
    (for [i (range 100)]
      [:div.pure-g.table-hover
       {:style {:padding-bottom "10px"
                :padding-top "10px"
                :border-bottom "solid 1px #e3e3e3"
                :cursor "pointer"}}
       [:div.pure-u-2-24.mik-flush-left
        (views-utils/position-absolute
         {:top "4px"}
         (radio-button "" false))]
       [:div.pure-u-6-24
        [:a.unstyled-link
         {:href "#" :style {:font-weight "300"}} "hello.mkv"]]
       [:div.pure-u-10-24
        [:div 
         (for [i (lorem/random-word-random-size 10)]
           [:a.unstyled-link.gray
            {:href "#"
             :style {:font-weight "300"}}  i " "])
         [:a.unstyled-link {:href "#"} "all"]]]
       [:div.pure-u-6-24.mik-flush-right
        [:a.unstyled-link {:href "#" :style
                           {:font-weight "300"}}
         "2017.02.03"]]])]
   [:div.mik-flush-right.gray
    "Truncated 23 results"]])

(defn dropzone []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :margin-top "1em" :margin-bottom "1em"
            :border "dashed 1px gray"}}
   [:a.pure-button {:href "#"}
    (views-utils/icon "file_upload")
    "Drop files here"]])

(defn filter-input []
  [:div.padded-as-button {:style {:position "relative"}}
   [:input.background-0
    {:type "text" :placeholder "Filter"
     :style {:width "100%" :height "2em" :background "white !important"}}]
   [:div {:style {:position "absolute" :right "30px" :top "20px"}}
    [:a.unstyled-link {:href "#"}
     "Clear"]]
   [:div {:style {:position "absolute" :right "75px" :top "23px"}}
    (views-utils/icon "search")]])

(defn drill
  []
  [:div.background-2
   [:div.pure-g
    [:div.pure-u-1-2
     ;; header
     [:div.pure-u-1.background-1
      [:div.pure-u-1-8
       [:div
        [:a.unstyled-link
         {:href "#" :style
          {:font-size "3em", :padding-top "0.5em",
           :padding-left "0.5em", :padding-right "0.5em"}}
         (views-utils/icon "keyboard_arrow_left")]]]
      [:div.pure-u-7-8
       (filter-input)]]
     
     ;; time facet
     [:div.pure-u-1-3.background-0
      [:div.padded-as-button
       (facet-group-select-time)]]
     
     ;; cloud facet
     [:div.pure-u-2-3
      [:div.background-1
       [:div {:style {:font-size "0.7em", :padding-bottom "2em"}}
        (selection-cloud)]]
      [:div.background-2
       [:div 
        (general-cloud)]]]]
    
    ;; found, files, and tag anew group
    [:div.pure-u-1-2.background-3
     [:div.padded-as-button
      ;; (found-group)
      (file-table)]]
    ;; will be able to see it only when selected
    [:div {:style {:position "fixed", :bottom "0px", :right "0px", :width "100%"}}
     [:div.pure-u-1
      (tagging-now-group)]]
    ]])

(defn choose-root
  []
  [:div
   {:style {:padding "5em", :border "dashed 1px gray"}}
   [:div.mik-flush-center.background-1
    [:a.padded-as-button.unstyled-link  {:href "#" :style {:font-size "4em"}}
     "Choose root directory"]
    [:div.mik-flush-center.background-1
     {:style
      {:padding "5em", :margin-top "1em", :margin-bottom "1em", :border "dashed 1px gray"}}
     [:a.pure-button {:href "#"}
      (views-utils/icon "file_upload")
      "Or Drop it here"]]]])

(defn processing
  []
  [:div.mik-flush-center.background-1
   {:style {:padding "5em" :border "dashed 1px gray"}}
   [:div
    [:a.pure-button {:href "#" :style {:font-size "4em"}}
     "Processing..."]
    [:div.mik-flush-center.background-1
     [:img {:src "/loading.gif"}]]]])

(defn main []
  (drill))

(ns miktau.edit-nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [miktau.autocomplete.views :as autocomplete-views]
            [miktau.tools :as utils]
            [re-frame.core :as refe]))

(defn back-button
  []
  [:div {:style {:position "relative"}}
   (if-not @(refe/subscribe [:undos?])
     [:a.unstyled-link.light-gray
      {:style
       {:font-size "5em" :cursor "default"}}
      [views-utils/icon "keyboard_arrow_left"]]
     [:a.unstyled-link.black-clickable
      {:on-click #(refe/dispatch [:undo])
       :style
       {:font-size "5em"}}
      [views-utils/icon "keyboard_arrow_left"]])])

(defn filter-input []
  [:div.pure-g
   [:div.pure-u-7-8
    [autocomplete-views/filter-input
     [:edit-nodes/get-app-data] true
     {:placeholder "New tag: [letters & numbers, lowercase, three to twenty symbols]"
      :submit-fn
      (fn [data]
       (if 
         (refe/dispatch [:edit-nodes/tag-click (keyword (str data))])
         identity))
      :validate-fn #(and  (string? %) (not (nil? (re-matches  #"^[a-zа-я0-9\_]{0,20}$" %))))}]]
     [:div.pure-u-1-8.mik-flush-right
      [:div.pure-button.pure-button-primary  {:style {:width "80%"}}
       [:div {:on-click #(refe/dispatch [:cloud/open-file-selecting-dialog])} "Add tag"]]]])

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))


(defn breadcrumbs []
  (let [breadcrumbs @(refe/subscribe [:edit-nodes/breadcrumbs])]
    [:div.mik-cut-top
     [:a.unstyled-link.red-clickable {:on-click #(refe/dispatch [:edit-nodes/clear]) :style {:padding-right "5px"}} "«Clear»"]
     [:span {:style {:padding-right "5px"}}]
     [:span.unstyled-link
      " [ Influencing: "   (:total-records breadcrumbs) " ] "]
     ;; tags to add
     (if-not (empty? (:tags-to-add breadcrumbs))
       [:span.unstyled-link.padded-as-button
        [:span.added-in "Adding"] " ( "
        (for [[item meta-item]  (for-every-and-last (:tags-to-add breadcrumbs))]
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        " )"]
       [:span])
     ;; tags to delete 
     (if-not (empty? (:tags-to-delete breadcrumbs))
       [:span.unstyled-link
        [:span.crossed-out "Removing"]
        " ( " 
        (for [[item meta-item]  (for-every-and-last (:tags-to-delete breadcrumbs))]
          ^{:key (:name item)}
          [:span
           [:a.unstyled-link.green-clickable {:on-click #(refe/dispatch (:on-click item))} (:name item)]
           (if-not (:last? meta-item) " • " " ")])
        " )"]
       [:span])
     ;; navigate to
     [:div.mik-flush-right.unstyled-link {:style {:padding-right "5px" }}
      "["
      [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:nodes/init-page #{}  #{} {}]) } "Nodes "]
      [:a.blue-clickable.unstyled-link {:href "#" :on-click #(refe/dispatch  [:cloud/init-page #{}  {}]) } "Cloud"]
      "]"]]))

(defn general-cloud-tag-item [tag]
  [:a.tag
   {:key  (:key-name tag)
    :on-click #(refe/dispatch (:on-click tag))
    :class
    (str
     (cond (:to-add? tag)  "added-in"
           (:to-delete? tag) "crossed-out"
           (:selected? tag) "selected"
           (:can-select? tag) "can-select"
           (:disabled? tag) "disabled"))
    :style
    {:font-size
     (str    (+ 0.6 (* 2.4  (tag :weighted-size))) "em")
     :cursor "pointer"}}
   (:name tag) " "])

(defn general-cloud []
  [:div
   (for [tag  @(refe/subscribe [:edit-nodes/cloud])]
     [:span {:key (:key-name tag)}
      [general-cloud-tag-item tag]])])

(defn main []
  [:div {:style {:height "100%"}}
   [:div.pure-u-1 {:style {:box-shadow "1px 1px 2px 0px gray"}}
    [:div.pure-u-1-24
     [back-button]]
    [:div.pure-u-23-24
     [:div.padded-as-button
      [filter-input]]
     [:div.padded-as-button {:style {:font-size "0.7em" :padding-bottom "1em"}}
      [breadcrumbs]]]]
   
   ;; tags to add/remove
   [:div.padded-as-button
    [general-cloud]]
   ;; changes to submit
   [:div.mik-flush-right.padded-as-button {:style {:margin-top "5em"}}
    [:a.pure-button {:on-click #(refe/dispatch [:edit-nodes/cancel-tagging])} [views-utils/icon "cancel"] " Cancel"]
    (if @(refe/subscribe [:edit-nodes/can-submit?])
      [:a.pure-button.pure-button-primary {:on-click #(refe/dispatch [:edit-nodes/submit-tagging])} [views-utils/icon "save"]   " Save"]
      [:a.pure-button.pure-button-primary.pure-button-disabled {} [views-utils/icon "save"]   " Save"])]])

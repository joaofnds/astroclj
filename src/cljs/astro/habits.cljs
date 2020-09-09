(ns astro.habits
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [astro.events :as events]))

(def new-habit-name (r/atom ""))
(defn habit-form []
  [:div
   [:input {:value @new-habit-name :on-change #(reset! new-habit-name (.-value (.-target %)))}]
   [:button {:on-click #(do (rf/dispatch [:habit.new @new-habit-name])
                            (reset! new-habit-name ""))}
    "create"]])

(defn present-date [date]
  (str (.toLocaleDateString date) " " (.toLocaleTimeString date)))

(defn show-habit [name activities register-activity]
  [:div
   [:h2 name]
   [:button {:on-click #(rf/dispatch [:habit.activity.new name])} "Register Activity"]
   [:ul (for [activity activities]
          [:li (present-date activity)])]])

(defn habit-list []
  (let [habits (rf/subscribe [:habits])]
    [:div
     [habit-form]
     [:div (for [[name activities] @habits]
             [show-habit name activities])]]))

(ns astro.habits
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [astro.events :as events]))

(def number-of-activity-classes 4)
(def year-in-days (* 53 7))
(def new-habit-name (r/atom ""))
(defn habit-form []
  [:div
   [:input {:value @new-habit-name :on-change #(reset! new-habit-name (.-value (.-target %)))}]
   [:button {:on-click #(do (rf/dispatch [:habit.new @new-habit-name])
                            (reset! new-habit-name ""))}
    "create"]])

(defn normalize [min max n]
  (/ (- n min) (- max min)))

(defn minmax [numbers]
  (let [min (apply min numbers)
        max (apply max numbers)]
    [min max]))

(defn present-date [date]
  (str (.toLocaleDateString date) " " (.toLocaleTimeString date)))

(defn add-days [date days-to-add]
  (let [dateCopy (js/Date. (.getTime date))]
    (.setDate dateCopy (+ days-to-add (.getDate date)))
    dateCopy))

(defn add-one-day [date]
  (add-days date 1))

(defn activity-count-by-date [timestamps]
  (frequencies (map #(.toDateString %) timestamps)))

(defn past-year-with-no-activity []
  (->> (- year-in-days)
       (add-days (js/Date.))
       (iterate add-one-day)
       (take year-in-days)
       (map #(vector (.toDateString %) 0))
       (into {})))

(defn fill-activity-year [activities]
  (merge-with + (past-year-with-no-activity) activities))

(defn fitter [min max classes]
  (fn [n]
    (inc (Math/floor (* classes (normalize min (inc max) n))))))

(defn build-classifier [values number-of-classes]
  (fitter (apply min values) (apply max values) number-of-classes))

(defn date-string-to-int [date-string]
  (.getTime (js/Date. date-string)))

(defn massage-activity-dates [dates]
  (->> dates
       activity-count-by-date
       fill-activity-year
       (into [])
       (sort-by #(-> % first date-string-to-int))))

(defn css-class-of-activity [class-number]
  (if (zero? class-number)
    "no-activity"
    (str "activitiy-" class-number)))

(defn activity-day [count date class]
  [:div {:key date
         :class (str "square " (css-class-of-activity class))
         :data-count count
         :data-date date
         :data-tooltip-text (str count " Activities on " date)}])

(defn histogram [activities-by-date-with-count]
  (let [classifier (build-classifier (map second activities-by-date-with-count) number-of-activity-classes)]
    [:div {:class "histogram"}
     (for [[date count] activities-by-date-with-count]
       (activity-day count date (if (zero? count) 0 (classifier count))))]))

(defn show-habit [name activities register-activity]
  [:div {:key name}
   [:h2 name]
   [:button
    {:on-click #(rf/dispatch [:habit.activity.new name])}
    "Register Activity"]
   (histogram (massage-activity-dates activities))])

(defn habit-list []
  (let [habits (rf/subscribe [:habits])]
    [:div
     [habit-form]
     (for [[name activities] @habits]
       [show-habit name activities])]))

(ns astro.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[astro started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[astro has shut down successfully]=-"))
   :middleware identity})

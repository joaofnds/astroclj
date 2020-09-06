(ns astro.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [astro.core-test]))

(doo-tests 'astro.core-test)


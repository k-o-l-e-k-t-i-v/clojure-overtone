;; Version 1.0beta26
;;
;;           http://github.com/overtone/emacs-live
;;
;;

(demo 1 (out 0 (pan2 (sin-osc 100))))

(definst bd1
  [fb 80
   fm 1000
   ft 0.01
   amp 1
   ampt 0.3
   ffb 40
   ffm 3000
   fft 0.05
   dist 0.1
   dism 10
   cbus 0]
  (let [t (in:kr cbus)
        env-amp (env-gen (perc 0 ampt amp -10) t 1 0 1)
        env-freq (env-gen (perc 0 ft fm 0) t 1 fb 1)
        env-flt (env-gen (perc 0 fft ffm 0) t 1 ffb 1)
        env-dist (env-gen (perc 0 dist dism 0) t 1 1 1)
        o (sin-osc-fb env-freq 0.7)
        f (resonz o env-flt 1)
        f (distort (* env-dist f))
        a (* f env-amp)]
    (out 0 (pan2 a))))

(def b1 (control-bus 1))
(def b2 (control-bus 1))
(def bbd1 (bd1 :cbus b1))
(def bbd2 (bd1 :cbus b2))
(kill bbd1)
(demo 100 (out b2 (impulse:kr 3)))
(demo 100 (out b1 (impulse:kr 4)))
(demo 100 (out b1 (impulse:kr 7)))
(ctl bbd2 :fb 50)
(ctl bbd1 :fm 400 :fb 20)
(bd1 :fb 40 :fm 200 :ffb 500 :ffm 200 :fft 0.4)
(def m1 (metronome 280))

(m1)

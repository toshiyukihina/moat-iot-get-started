# /moat/v1/simple-example/vibrationdevice
class VibrationDevice < MoatModel

  def initialize(device_name=nil)
    @device_name = device_name
  end

  def self.stub(device_name)
    VibrationDevice.new(device_name)
  end

  def vibrate_async
    now = Time.new
    SysDmjob.new({
      job_service_id: Moat::VIBRATE_DEVICE,
      name: @device_name,
      activated_at: now.rfc2822,
      expired_at: (now + 15.minutes).rfc2822,
      arguments: {
        #
        # See http://developer.android.com/reference/android/os/Vibrator.html#vibrate(long[], int)
        # The first value indicates the number of milliseconds to wait before turning the vibrator on.
        # The next value indicates the number of milliseconds for which to keep the vibrator on before turning it off.
        # Subsequent values alternate between durations in milliseconds to turn the vibrator off or to turn the vibrator on.
        #
        # S-O-S * 2
        options: [0, 0, 0 \
          ,200, 200, 200, 200, 200 \
          ,500 \
          ,500, 200, 500, 200, 500 \
          ,500 \
          ,200, 200, 200, 200, 200 \
          ,1000 \
          ,200, 200, 200, 200, 200 \
          ,500 \
          ,500, 200, 500, 200, 500 \
          ,500 \
          ,200, 200, 200, 200, 200 \
          ]
      }
    }).save
  end

  # For the resource type attribute
  class Image < MoatModel
  end
end
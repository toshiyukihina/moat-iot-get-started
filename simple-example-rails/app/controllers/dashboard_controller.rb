class DashboardController < ApplicationController
  
  def index
    @vibration_devices = VibrationDevice.find(:all, params: { r: "get"}) || []
    @shake_events = {}
    @devices = SysDevice.find || []
    @devices.each do |d|
      @shake_events[d.name] = ShakeEvent.find(:all, params: {
        offset: params[:offset], limit: params[:limit]
      }, device: d.uid) # device: is a MoatModel class specific argument.
    end
    @job_list = SysDmjob.find(:all) || []
    @job_histories = RequestHistory.find(:all) || []
  end
  
  def delete_device
    SysDevice.delete(params[:device_uid]) if params[:device_uid]
    redirect_to action: 'index'
  rescue
    flash[:error] = "Cancel the ongoing requests prior to removing the device."
    redirect_to action: 'index'
  end
  
  def vibrate
    if params[:name]
      now = Time.new
      SysDmjob.new({
        job_service_id: Moat::VIBRATE_DEVICE,
        name: params[:name],
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
    redirect_to action: 'index'
  end
  
  def cancel
    SysDmjob.delete(params[:uid]) if params[:uid]
    redirect_to action: 'index'
  rescue
    flash[:error] = "Nothing to cancel."
    redirect_to action: 'index'
  end
  
  def delete_all_shake_events
    ShakeEvent.delete(params[:uids], {device: params[:device_uid]}) if params[:uids]
    redirect_to action: 'index'
  end
end

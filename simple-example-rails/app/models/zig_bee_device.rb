# /moat/v1/simple-example/zigbeedevice
class ZigBeeDevice < MoatModel
  
  attr_accessor :device_name
  
  def self.stub(device_name, uid)
    z = ZigBeeDevice.new
    z.device_name = device_name
    z.uid = uid
    z
  end
    
  def show_text_on_lcd_async(text)
    now = Time.new
    SysDmjob.new({
      job_service_id: Moat::SHOW_TEXT_ON_LCD,
      name: device_name,
      activated_at: now.rfc2822,
      expired_at: (now + 15.minutes).rfc2822,
      arguments: {
        uid: uid,
        text: text
      }
    }).save
  end

  def inquire_temperature_async
    now = Time.new
    SysDmjob.new({
      job_service_id: Moat::SHOW_TEXT_ON_LCD,
      name: device_name,
      activated_at: now.rfc2822,
      expired_at: (now + 15.minutes).rfc2822,
      arguments: {
        uid: uid
      }
    }).save
  end
end
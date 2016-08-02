# encoding: utf-8
module LogStash
  module Api
    module Modules
      class Logging < ::LogStash::Api::Modules::Base

        post "/" do
          level = params["level"]
          path = params["module"] || ""
          if level.nil?
            status 400
            respond_with({"error" => "[level] must be specified"})
          else
            LogStash::Logging::Logger::configure_logging(path, level)
            respond_with({"acknowledged" => true})
          end
        end

      end
    end
  end
end

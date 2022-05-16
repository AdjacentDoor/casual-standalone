package se.laz.casual.standalone


import se.laz.casual.api.buffer.CasualBuffer
import se.laz.casual.api.buffer.ServiceReturn
import se.laz.casual.api.buffer.type.JsonBuffer
import se.laz.casual.api.flags.AtmiFlags
import se.laz.casual.api.flags.ErrorState
import se.laz.casual.api.flags.Flag
import se.laz.casual.api.flags.ServiceReturnState
import se.laz.casual.network.ProtocolVersion
import spock.lang.Shared
import spock.lang.Specification

class CallerTest extends Specification
{
   @Shared
   def domainName = 'Test-Domain'
   @Shared
   def domainId = UUID.randomUUID()
   @Shared
   ProtocolVersion protocolVersion = ProtocolVersion.VERSION_1_0
   @Shared
   def address = Mock(InetSocketAddress)
   @Shared
   def replyBuffer = JsonBuffer.of('{"name":"John Doe"}')
   @Shared
   Caller instance
   @Shared
   Flag<AtmiFlags> noFlags = Flag.of(AtmiFlags.NOFLAG)

   def setup()
   {
      when:
      instance = of(address, protocolVersion, domainId, domainName)
      then:
      instance != null
   }

   def 'service call'()
   {
      when:
      def actual = instance.tpcall('some service', Mock(CasualBuffer),  noFlags)
      then:
      actual.getReplyBuffer() == replyBuffer
   }

   Caller of(InetSocketAddress address, ProtocolVersion protocolVersion, UUID domainId, String domainName)
   {
      return Mock(Caller.class) {
         tpcall(_, _, _) >>{
            ServiceReturn serviceReturn = new ServiceReturn(replyBuffer, ServiceReturnState.TPSUCCESS, ErrorState.OK, 0)
            return serviceReturn
         }
      }
   }

}

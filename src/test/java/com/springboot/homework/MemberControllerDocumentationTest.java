package com.springboot.homework;

import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.google.gson.Gson;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.web.servlet.function.RequestPredicates.accept;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerDocumentationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void getMemberTest() throws Exception {
//        given
        long memberId = 1L;
        MemberDto.Response response =
                new MemberDto.Response(memberId, "bana@rabbit.com", "바니","010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE, new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);
//        when
        ResultActions getActions = mockMvc.perform(
                get("/v11/members/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
//        then
        getActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(response.getMemberId()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(response.getMemberStatus()))
                .andExpect(jsonPath("$.data.stamp").value(response.getStamp()))
                .andDo(document(
                        "get-Member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("멤버 ID")
                        ),
//                        request 요청
//                        requestFields(
//                                List.of(
//                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 ID")
////                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
//                                )
//                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("멤버 ID"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("핸드폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING).description("멤버 상태"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수")
                                )
                        )
                ));
    }

    @Test
    public void getMembersTest() throws Exception {
//        given
        long memberId = 1L;
        Page<Member> pageMembers = new PageImpl<>(List.of(new Member()));
        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(memberId, "bana@rabbit.com", "바니","010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.Response(memberId, "bana@preso.com", "바나","010-1234-5670", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.Response(memberId, "bani@rabbit.com", "바나나","010-1234-5679", Member.MemberStatus.MEMBER_ACTIVE, new Stamp())
        );

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);
//        when

//        then
    }

    @Test
    public void deleteMemberTest() throws Exception {
//        given
        long memberId = 1L;
        doNothing().when(memberService).deleteMember(Mockito.anyLong());
//        when
        ResultActions actions = mockMvc.perform(
                delete("/v11/members/{memberId}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
//        then
        actions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-Member",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        pathParameters(
                                parameterWithName("memberId").description("삭제 회원 Id")
                        )
                ));
    }
}

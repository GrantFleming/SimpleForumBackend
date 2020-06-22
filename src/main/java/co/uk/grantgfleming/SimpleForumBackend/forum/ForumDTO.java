package co.uk.grantgfleming.SimpleForumBackend.forum;

import lombok.Data;

@Data
public class ForumDTO {

    private Long id;

    private String name;

    private String description;

    private String creator;

    public static ForumDTO fromForum(Forum forum) {
        ForumDTO forumDTO = new ForumDTO();
        forumDTO.setId(forum.getId());
        forumDTO.setName(forum.getName());
        forumDTO.setDescription(forum.getDescription());
        forumDTO.setCreator(forum.getUser().getAlias());
        return forumDTO;
    }
}

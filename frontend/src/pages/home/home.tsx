// Home page — main landing page of the application

import SectionCard from "../../components/common/sectionCard";
import pokedexIcon from "../../assets/section_icons/Pokedex.png";
import teamsIcon from "../../assets/section_icons/Teams.png";
import simulatorIcon from "../../assets/section_icons/Simulator.png";
import trainerIcon from "../../assets/section_icons/Trainer.png";
import "./home.css";

// Data for each section card shown on the home page
const sections = [
  {
    title: "PokeDex",
    description:
      "The greatest Pokemon encyclopedia that reunites all its available knowledge",
    imageSrc: pokedexIcon,
    imageAlt: "PokeDex section icon",
    linkTo: "/pokedex",
  },
  {
    title: "Teams",
    description:
      "Build your own teams with every detail and precise data for excellent viability results",
    imageSrc: teamsIcon,
    imageAlt: "Teams section icon",
    linkTo: "/teams",
  },
  {
    title: "Simulator",
    description:
      "Try your own teams in simulated battles and take note of your victory chances",
    imageSrc: simulatorIcon,
    imageAlt: "Simulator section icon",
    linkTo: "/simulator",
  },
  {
    title: "Trainer Card",
    description:
      "Edit and customize your TrainerForge profile. See your stats, achievements and more",
    imageSrc: trainerIcon,
    imageAlt: "Trainer Card section icon",
    linkTo: "/trainer-card",
  },
];

function Home() {
  return (
    <main className="home">
      <section className="home__hero" aria-label="Hero">
        <img
          src="/hero.png"
          alt="TrainerForge hero"
          className="home__hero-image"
          loading="eager"
        />
      </section>

      <section className="home__sections" aria-label="Main sections">
        <div className="home__sections-grid">
          {sections.map((section) => (
            <SectionCard key={section.title} {...section} />
          ))}
        </div>
      </section>
    </main>
  );
}

export default Home;
